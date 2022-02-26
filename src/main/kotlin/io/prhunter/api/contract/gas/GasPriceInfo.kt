package io.prhunter.api.contract.gas

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class GasPriceInfo(
    @JsonProperty("baseFee")
    val baseFee: BigDecimal
)
