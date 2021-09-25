package io.prhunter.api.bounty.api

import java.math.BigDecimal

data class UpdateBountyRequest(
    val title: String,
    val body: String,
    val languages: List<String>,
    val bountyValue: BigDecimal,
    val bountryCurrency: String
)