package io.prhunter.api.contract

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider


@Service
class ContractService {

    private val log = KotlinLogging.logger {}
    private val web3j = Web3j.build(HttpService("https://eth-ropsten.alchemyapi.io/v2/c2fNFavCNrrK4dSnkfBGmJ7oSNqfxZ6z"))
    private val contractAddress = "0x06783aE8EF55191730244984CA2FDAEd197ebCc0"

//    private val web3j = Web3j.build(HttpService("https://rinkeby.infura.io/v3/373543e00dc9456b98aec7048949799c"))
//    private val contractAddress = "0x3f8E8137D857DF24522e8F696a2EaE5191D501b2"

    final val bountyFactory: BountyFactory = BountyFactory.load(
        contractAddress,
        web3j,
        Credentials.create("d2db9150ba3248512c238b69e7f51a15014b1ba8919c7651c2e47c4b5c49ff6f"),
        DefaultGasProvider()
    )
//    private val flowable = bountyFactory.bountyCreatedEventFlowable(EthFilter()).subscribe { bountyCreated ->
//        log.info { "Bounty created: ${bountyCreated.bounty}" }
//    }
}