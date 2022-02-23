package io.prhunter.api.contract

import io.prhunter.api.crypto.CryptoCurrency
import org.springframework.stereotype.Service

@Service
class ContractServiceResolver(
    private val ethContractService: EthContractService,
    private val bscContractService: BscContractService
){

    fun getContractService(currency: CryptoCurrency): ContractService {
        return when(currency){
            CryptoCurrency.ETH -> ethContractService
            CryptoCurrency.BNB -> bscContractService
        }
    }
}