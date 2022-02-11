package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.bounty.BountyStatus
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
class ContractService(
    private val bountyRepository: BountyRepository,
    @Value("\${crypto.alchemyUrl}") val alchemyUrl: String,
    @Value("\${crypto.ethPkey}") val ethPrivateKey: String,
    @Value("\${crypto.bountyFactoryEthAddress}") val bountyFactoryEthAddress: String,
) {

    private val log = KotlinLogging.logger {}
    private final val web3j = Web3j.build(HttpService(alchemyUrl))
    private final val credentials = Credentials.create(ethPrivateKey)
    private final val bountyFactory: BountyFactory = BountyFactory.load(
        bountyFactoryEthAddress,
        web3j,
        credentials,
        DefaultGasProvider()
    )

    @Suppress("UNCHECKED_CAST")
    fun checkPendingContracts() {
        val pendingBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
        pendingBounties.forEach { bounty ->
            val bountyAddressOpt = bountyFactory.allBounties(bounty.id.toString()).send()
            if (bountyAddressOpt != null) {
                val contractId = getContractBountyId(bountyAddressOpt)
                if (contractId == bounty.id.toString()) {
                    log.info { "Bounty ${bounty.id} deployed successfully, activating" }
                    bounty.bountyStatus = BountyStatus.ACTIVE
                    bountyRepository.save(bounty)
                }
            }
        }
    }

    fun payoutBounty(targetAddress: String, bounty: io.prhunter.api.bounty.Bounty) {
        try {
            val address = bountyFactory.allBounties(bounty.id.toString()).send()
            val bountyContract = Bounty.load(address, web3j, credentials, DefaultGasProvider())
            val tx = bountyContract.payoutBounty(targetAddress).send()
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