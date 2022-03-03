package io.prhunter.api.crypto

import io.prhunter.api.contract.BlockchainInfo
import io.prhunter.api.contract.ContractService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class CryptoResolver(
    private val ethBlockchainInfo: BlockchainInfo,
    private val bscBlockchainInfo: BlockchainInfo,
    @Qualifier("ethContractService")
    private val ethContractService: ContractService,
    @Qualifier("bscContractService")
    private val bscContractService: ContractService
) {

    fun getBlockchainUrl(address: String?, cryptoCurrency: CryptoCurrency): String? {
        return if (address != null) {
            when (cryptoCurrency) {
                CryptoCurrency.ETH -> ethBlockchainInfo.etherScanUrl + "/address/${address}"
                CryptoCurrency.BNB -> bscBlockchainInfo.etherScanUrl + "/address/${address}"
            }
        } else null
    }

    fun getContractService(currency: CryptoCurrency): ContractService {
        return when (currency) {
            CryptoCurrency.ETH -> ethContractService
            CryptoCurrency.BNB -> bscContractService
        }
    }
}