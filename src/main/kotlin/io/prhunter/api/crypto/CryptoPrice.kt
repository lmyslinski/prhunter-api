package io.prhunter.api.crypto

import java.math.BigDecimal

data class CryptoPrice(
    val currency: String,
    val usdValue: BigDecimal
)