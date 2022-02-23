package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.GithubAppService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.exceptions.ContractCallException
import java.time.Instant

@Service
@Profile("!test")
class EthContractService(
    private val bountyRepository: BountyRepository,
    private val githubAppService: GithubAppService,
    private val lazyGasProvider: LazyGasProvider,
    @Value("\${crypto.alchemyUrl}") private val alchemyUrl: String,
    @Value("\${crypto.ethPkey}") private val ethPrivateKey: String,
    @Value("\${crypto.bountyFactoryEthAddress}") private val bountyFactoryEthAddress: String
) : ContractService {

    private val log = KotlinLogging.logger {}
    private val web3j = Web3j.build(HttpService(alchemyUrl))
    private val credentials = Credentials.create(ethPrivateKey)
    private val oneHour = 3600L

    private val bountyFactory: BountyFactory = BountyFactory.load(
        bountyFactoryEthAddress,
        web3j,
        credentials,
        lazyGasProvider
    )

    @Suppress("UNCHECKED_CAST")
    override fun periodicBountyUpdate() {
        val pendingBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
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

    private fun activateBounty(bounty: io.prhunter.api.bounty.Bounty, bountyAddress: String) {
        log.info { "Bounty ${bounty.id} deployed successfully, marking as ACTIVE" }
        try {
            bounty.bountyStatus = BountyStatus.ACTIVE
            bounty.blockchainAddress = bountyAddress
            bountyRepository.save(bounty)
            githubAppService.newBountyComment(bounty)
        } catch (ex: Throwable) {
            log.error(ex) { "An error was occurred while updating bounty ${bounty.id}" }
        }
    }

    override fun payoutBounty(targetAddress: String, bounty: io.prhunter.api.bounty.Bounty) {
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

    override fun getBountyFactoryAddress(): String = bountyFactoryEthAddress

    private fun getContractBountyId(bountyAddressOpt: String): String? {
        return try {
            Bounty.load(bountyAddressOpt, web3j, credentials, lazyGasProvider).bountyId().send()
        } catch (ex: ContractCallException) {
            log.warn { "Could not load contract at $bountyAddressOpt" }
            null
        }
    }
}