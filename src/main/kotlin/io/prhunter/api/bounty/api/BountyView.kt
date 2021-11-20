package io.prhunter.api.bounty.api

import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import java.math.BigDecimal
import java.time.Instant

data class BountyView(
    val id: Long,
    val repoId: Long,
    val repoOwner: String,
    val repoName: String,
    val issueId: Long,
    val issueNumber: Long,
    val githubUserId: Long,
    val title: String,
    val body: String,
    val languages: Array<String>,
    val tags: Array<String>,
    val experience: Experience,
    val bountyType: BountyType,
    val bountyValue: BigDecimal,
    val bountyValueUsd: BigDecimal,
    val bountyCurrency: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

