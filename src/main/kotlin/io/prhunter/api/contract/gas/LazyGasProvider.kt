package io.prhunter.api.contract.gas

import io.prhunter.api.contract.BlockchainInfo
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

// a gas provider that gets the price when a tx is actually called, not when first loading a contract
class LazyGasProvider(
    private val gasPriceResolver: GasPriceResolver,
    private val blockchainInfo: BlockchainInfo
): ContractGasProvider {
    override fun getGasPrice(contractFunc: String?): BigInteger = gasPriceResolver.getGasPrice(blockchainInfo)
    override fun getGasPrice(): BigInteger = gasPriceResolver.getGasPrice(blockchainInfo)
    override fun getGasLimit(contractFunc: String?): BigInteger = gasPriceResolver.getGasLimit(blockchainInfo)
    override fun getGasLimit(): BigInteger = gasPriceResolver.getGasLimit(blockchainInfo)
}