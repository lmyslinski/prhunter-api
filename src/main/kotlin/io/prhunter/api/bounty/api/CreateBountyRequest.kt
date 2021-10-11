package io.prhunter.api.bounty.api

import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import java.math.BigDecimal

data class CreateBountyRequest(
    val repoId: Long,
    val issueId: Long,
    val title: String,
    val body: String,
    val languages: List<String>,
    val tags: List<String>,
    val experience: Experience,
    val bountyType: BountyType,
    val bountyValue: BigDecimal,
    val bountyCurrency: String
)