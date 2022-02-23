package io.prhunter.api.contract.gas

import java.math.BigDecimal

data class GasPriceInfo(
    val avgTx: BigDecimal,
    val avgTime: BigDecimal
)
