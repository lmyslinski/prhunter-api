package io.prhunter.api.contract

import io.prhunter.api.crypto.CryptoCurrency
import java.math.BigInteger

data class BlockchainInfo(
    val currency: CryptoCurrency,
    val bountyFactoryAddress: String,
    val walletPkey: String,
    val etherScanUrl: String,
    val rpcUrl: String,
    val gasLimit: BigInteger,
    val testNet: Boolean,
)