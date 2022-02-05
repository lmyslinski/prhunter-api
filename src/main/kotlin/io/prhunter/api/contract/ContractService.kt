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
    private val contractAddress = "0x4518188E9f6fB8C8D38eb74e44fC2c1F91B60227"
    private final val bountyFactory: BountyFactory = BountyFactory.load(
        contractAddress,
        web3j,
        Credentials.create("d2db9150ba3248512c238b69e7f51a15014b1ba8919c7651c2e47c4b5c49ff6f"),
        DefaultGasProvider()
    )

    @Suppress("UNCHECKED_CAST")
    @Scheduled(cron = EVERY_30_SECONDS)
    fun getMeBlockChainData() {
        val allContracts: List<String> = bountyFactory.all.send() as List<String>
        val pendingBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
        val bountyContract = Bounty.load(allContracts.get(0), web3j, Credentials.create("d2db9150ba3248512c238b69e7f51a15014b1ba8919c7651c2e47c4b5c49ff6f"), DefaultGasProvider())
        val secret = bountyContract.bountySecret().send()
        // TODO migrate bounty secret to bounty id, use a mapping instead of array and access by key
        log.info { allContracts }
    }

}