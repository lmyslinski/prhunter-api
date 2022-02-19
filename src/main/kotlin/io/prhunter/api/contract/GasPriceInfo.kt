package io.prhunter.api.contract

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class GasPriceInfo(
    // value is in 10x gwei
    val average: Long,
    // the wait time is in minutes
    @JsonProperty("avgWait")
    val avgWait: Long,
    // value is in 10x gwei
    @JsonProperty("safeLow")
    val safeLow: Long,
    // the wait time is in minutes
    @JsonProperty("safeLowWait")
    val safeLowWait: Long
)
