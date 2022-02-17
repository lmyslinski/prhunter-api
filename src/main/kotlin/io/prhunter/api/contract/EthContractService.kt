package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.GithubAppService
import io.prhunter.api.github.GithubUserService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.exceptions.ContractCallException
import org.web3j.tx.gas.DefaultGasProvider

@Service
@Profile("!test")
class EthContractService(
    private val bountyRepository: BountyRepository,
    private val githubAppService: GithubAppService,
    @Value("\${crypto.alchemyUrl}") val alchemyUrl: String,
    @Value("\${crypto.ethPkey}") val ethPrivateKey: String,
    @Value("\${crypto.bountyFactoryEthAddress}") val bountyFactoryEthAddress: String,
) : ContractService {

    private val log = KotlinLogging.logger {}
    private val web3j = Web3j.build(HttpService(alchemyUrl))
    private val credentials = Credentials.create(ethPrivateKey)
    private val bountyFactory: BountyFactory = BountyFactory.load(
        bountyFactoryEthAddress,
        web3j,
        credentials,
        DefaultGasProvider()
    )

    @Suppress("UNCHECKED_CAST")
    override fun checkPendingContracts() {
        val pendingBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
        pendingBounties.forEach { bounty ->
            val bountyAddressOpt = bountyFactory.allBounties(bounty.id.toString()).send()
            if (bountyAddressOpt != null && bountyAddressOpt != "0x0000000000000000000000000000000000000000") {
                val contractId = getContractBountyId(bountyAddressOpt)
                if (contractId == bounty.id.toString()) {
                    activateBounty(bounty)
                }
            }
        }
    }

    private fun activateBounty(bounty: io.prhunter.api.bounty.Bounty) {
        log.info { "Bounty ${bounty.id} deployed successfully, activating" }
        try{
            bounty.bountyStatus = BountyStatus.ACTIVE
            bountyRepository.save(bounty)
            githubAppService.newBountyComment(bounty)
        }catch (ex: Throwable){
            log.error(ex) { "An error was occurred while activating bounty ${bounty.id}" }
        }
    }

    override fun payoutBounty(targetAddress: String, bounty: io.prhunter.api.bounty.Bounty) {
        try {
            val address = bountyFactory.allBounties(bounty.id.toString()).send()
            val bountyContract = Bounty.load(address, web3j, credentials, DefaultGasProvider())
            bountyContract.payoutBounty(targetAddress).send()
            // TODO verify the TX was successful
            // maybe add the TX in an event so that we can monitor what's going on
            log.info { "Sent a payout transaction from bounty $address to $targetAddress" }
        } catch (ex: Throwable) {
            log.error(ex) { "Fatal error, could not load bounty in order to payout bounty" }
        }
    }

    private fun getContractBountyId(bountyAddressOpt: String): String? {
        return try {
            Bounty.load(bountyAddressOpt, web3j, credentials, DefaultGasProvider()).bountyId().send()
        } catch (ex: ContractCallException) {
            log.warn { "Could not load contract at $bountyAddressOpt" }
            null
        }
    }
}