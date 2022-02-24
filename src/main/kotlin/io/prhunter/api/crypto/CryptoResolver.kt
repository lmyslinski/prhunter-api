package io.prhunter.api.crypto

import io.prhunter.api.contract.BlockchainInfo
import io.prhunter.api.contract.BscContractService
import io.prhunter.api.contract.ContractService
import io.prhunter.api.contract.EthContractService
import org.springframework.stereotype.Service

@Service
class CryptoResolver(
    private val ethBlockchainInfo: BlockchainInfo,
    private val bscBlockchainInfo: BlockchainInfo,
    private val ethContractService: EthContractService,
    private val bscContractService: BscContractService
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