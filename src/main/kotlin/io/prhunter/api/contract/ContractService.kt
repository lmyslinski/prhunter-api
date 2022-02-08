package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.bounty.BountyStatus
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider

@Service
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
                val bountyContract = Bounty.load(bountyAddressOpt, web3j, credentials, DefaultGasProvider())
                if (bountyContract.bountyId().send() == bounty.id.toString()) {
                    log.info { "Bounty ${bounty.id} deployed successfully, activating" }
                    bounty.bountyStatus = BountyStatus.ACTIVE
                    bountyRepository.save(bounty)
                }
            }
        }
    }
}