package io.prhunter.api.bounty.api

import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import java.math.BigDecimal

data class CreateBountyRequest(
    val repoOwner: String,
    val repoName: String,
    val issueNumber: Long,
    val title: String,
    val problemStatement: String,
    val acceptanceCriteria: String,
    val languages: List<String>,
    val tags: List<String>,
    val experience: Experience,
    val bountyType: BountyType,
    val bountyValue: BigDecimal,
    val bountyCurrency: String,
    val transactionHash: String
)