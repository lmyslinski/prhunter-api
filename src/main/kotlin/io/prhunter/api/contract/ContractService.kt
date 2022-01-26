package io.prhunter.api.contract

import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

@Service
class ContractService {

    val web3j = Web3j.build(HttpService("https://mainnet.infura.io/v3/373543e00dc9456b98aec7048949799c"))
}