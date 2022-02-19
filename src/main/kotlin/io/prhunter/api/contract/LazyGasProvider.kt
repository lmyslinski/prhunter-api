package io.prhunter.api.contract

import org.springframework.stereotype.Service
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

// a gas provider that gets the price when a tx is actually called, not when first loading a contract
@Service
class LazyGasProvider(
    private val gasPriceResolver: GasPriceResolver
): ContractGasProvider {
    override fun getGasPrice(contractFunc: String?): BigInteger = gasPriceResolver.getGasPrice()
    override fun getGasPrice(): BigInteger = gasPriceResolver.getGasPrice()
    override fun getGasLimit(contractFunc: String?): BigInteger = gasPriceResolver.getGasLimit()
    override fun getGasLimit(): BigInteger = gasPriceResolver.getGasLimit()
}