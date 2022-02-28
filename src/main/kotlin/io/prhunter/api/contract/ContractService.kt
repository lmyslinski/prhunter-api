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
import java.time.Instant

abstract class ContractService(
    private val bountyRepository: BountyRepository,
    private val githubAppService: GithubAppService,
) {

    protected abstract val bountyFactory: BountyFactory
    protected abstract val web3j: Web3j
    protected abstract val credentials: Credentials
    protected abstract val lazyGasProvider: LazyGasProvider
    protected abstract val blockchainInfo: BlockchainInfo

    protected val log = KotlinLogging.logger {}
    private val oneHour = 3600L

    @Suppress("UNCHECKED_CAST")
    fun periodicBountyUpdate() {
        val pendingBounties = bountyRepository.findAllByBountyStatusAndBountyCurrency(BountyStatus.PENDING, blockchainInfo.currency.name)
        pendingBounties.forEach { activateIfDeployed(it) }
        val failedBounties = pendingBounties.filter { it.createdAt.isBefore(Instant.now().minusSeconds(oneHour)) }
        failedBounties.forEach { failIfNotDeployedForTooLong(it) }
        val expiredBounties =
            bountyRepository.findAllByBountyStatusAndExpiresAtLessThan(BountyStatus.ACTIVE, Instant.now())
        expiredBounties.forEach { cleanupExpiredBounty(it) }
    }

    private fun cleanupExpiredBounty(bounty: io.prhunter.api.bounty.Bounty) {
        log.info { "Cleaning up expired bounty ${bounty.id}" }
        try {
            val bountyContract = Bounty.load(bounty.blockchainAddress, web3j, credentials, lazyGasProvider)
            val timestamp = bountyContract.expiryTimestamp().send()
            log.debug { "Contract timestamp: $timestamp" }
            bountyContract.claimTimeout().send()
            bounty.bountyStatus = BountyStatus.EXPIRED
            bountyRepository.save(bounty)
            githubAppService.expireBountyComment(bounty)
            log.info { "Cleaned up expired bounty ${bounty.id}" }
        } catch (ex: Throwable) {
            log.error(ex) { "An error was occurred while updating bounty ${bounty.id}" }
        }
    }

    private fun activateIfDeployed(bounty: io.prhunter.api.bounty.Bounty) {
        // TODO add try-catch to all bountyFactory functions,
        //  probably best to wrap it as a separate service with build in error handling
        val bountyAddressOpt = bountyFactory.allBounties(bounty.id.toString()).send()
        if (bountyAddressOpt != null && bountyAddressOpt != "0x0000000000000000000000000000000000000000") {
            val contractId = getContractBountyId(bountyAddressOpt)
            if (contractId == bounty.id.toString()) {
                activateBounty(bounty, bountyAddressOpt)
            }
        }
    }

    private fun failIfNotDeployedForTooLong(bounty: io.prhunter.api.bounty.Bounty) {
        log.info { "Bounty ${bounty.id} failed to deploy within 1h, marking as FAILED" }
        try {
            bounty.bountyStatus = BountyStatus.FAILED
            bountyRepository.save(bounty)
        } catch (ex: Throwable) {
            log.error(ex) { "An error was occurred while updating bounty ${bounty.id}" }
        }
        log.info { "Bounty ${bounty.id} failed to deploy within 1h, marking as failed" }
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
            log.error(ex) { "Fatal error, could not payout bounty" }
            throw ex
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

    fun getBountyFactoryAddress(): String = bountyFactory.contractAddress
}