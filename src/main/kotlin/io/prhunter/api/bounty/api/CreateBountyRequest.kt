package io.prhunter.api.bounty.api

import java.math.BigDecimal

data class CreateBountyRequest(
    val repoId: Long,
    val issueId: Long,
    val title: String,
    val body: String,
    val languages: List<String>,
    val bountyValue: BigDecimal,
    val bountyCurrency: String
)