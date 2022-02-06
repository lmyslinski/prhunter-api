package io.prhunter.api.contract

import io.prhunter.api.bounty.BountyRepository
import io.prhunter.api.bounty.BountyStatus
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import javax.annotation.PostConstruct


@Service
class ContractService(
    private val bountyRepository: BountyRepository
) {

    companion object {
        const val EVERY_30_SECONDS = "0 */2 * * * *"
    }

    private val log = KotlinLogging.logger {}
    private val web3j =
        Web3j.build(HttpService("https://eth-ropsten.alchemyapi.io/v2/c2fNFavCNrrK4dSnkfBGmJ7oSNqfxZ6z"))
    private val contractAddress = "0x153Bab3d11fE9e2f90b9747060855fEbCf31F92C"
    private final val bountyFactory: BountyFactory = BountyFactory.load(
        contractAddress,
        web3j,
        Credentials.create("d2db9150ba3248512c238b69e7f51a15014b1ba8919c7651c2e47c4b5c49ff6f"),
        DefaultGasProvider()
    )

    @Suppress("UNCHECKED_CAST")
    @Scheduled(cron = EVERY_30_SECONDS)
    fun getMeBlockChainData() {
        val pendingBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
        pendingBounties.forEach{ bounty ->
            val bountyAddressOpt = bountyFactory.allBounties(bounty.id.toString()).send()
            if(bountyAddressOpt != null){
                val bountyContract = Bounty.load(bountyAddressOpt, web3j, Credentials.create("d2db9150ba3248512c238b69e7f51a15014b1ba8919c7651c2e47c4b5c49ff6f"), DefaultGasProvider())
                if(bountyContract.bountyId().send() == bounty.id.toString()){
                    log.info { "Bounty ${bounty.id} deployed successfully, activating" }
                    bounty.bountyStatus = BountyStatus.ACTIVE
                    bountyRepository.save(bounty)
                }
            }
        }
    }

}