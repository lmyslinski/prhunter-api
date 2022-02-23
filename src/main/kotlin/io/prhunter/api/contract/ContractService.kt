package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.contract.abi.Bounty
import io.prhunter.api.contract.abi.BountyFactory
import io.prhunter.api.contract.gas.LazyGasProvider
import io.prhunter.api.github.GithubAppService
import mu.KotlinLogging
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.exceptions.ContractCallException

abstract class ContractService(
    private val bountyRepository: BountyRepository,
    private val githubAppService: GithubAppService,
) {

    protected abstract val bountyFactory: BountyFactory
    protected abstract val web3j: Web3j
    protected abstract val credentials: Credentials
    protected abstract val lazyGasProvider: LazyGasProvider

    protected val log = KotlinLogging.logger {}

    fun checkPendingContracts() {
        val pendingBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
        pendingBounties.forEach { bounty ->
            val bountyAddressOpt = bountyFactory.allBounties(bounty.id.toString()).send()
            if (bountyAddressOpt != null && bountyAddressOpt != "0x0000000000000000000000000000000000000000") {
                val contractId = getContractBountyId(bountyAddressOpt)
                if (contractId == bounty.id.toString()) {
                    activateBounty(bounty, bountyAddressOpt)
                }
            }
        }
    }

    fun payoutBounty(targetAddress: String, bounty: io.prhunter.api.bounty.Bounty) {
        try {
            val address = bountyFactory.allBounties(bounty.id.toString()).send()
            val bountyContract = Bounty.load(address, web3j, credentials, lazyGasProvider)
            bountyContract.payoutBounty(targetAddress).send()
            // TODO verify the TX was successful
            // maybe add the TX in an event so that we can monitor what's going on
            log.info { "Successfully submitted a payout bounty tx from bounty $address to $targetAddress" }
        } catch (ex: Throwable) {
            log.error(ex) { "Fatal error, could not load bounty in order to payout bounty" }
        }
    }

    private fun getContractBountyId(bountyAddressOpt: String): String? {
        return try {
            Bounty.load(bountyAddressOpt, web3j, credentials, lazyGasProvider).bountyId().send()
        } catch (ex: ContractCallException) {
            log.warn { "Could not load contract at $bountyAddressOpt" }
            null
        }
    }

    private fun activateBounty(bounty: io.prhunter.api.bounty.Bounty, bountyAddress: String) {
        log.info { "Bounty ${bounty.id} deployed successfully, activating" }
        try {
            bounty.bountyStatus = BountyStatus.ACTIVE
            bounty.blockchainAddress = bountyAddress
            bountyRepository.save(bounty)
            githubAppService.newBountyComment(bounty)
        } catch (ex: Throwable) {
            log.error(ex) { "An error was occurred while activating bounty ${bounty.id}" }
        }
    }
}