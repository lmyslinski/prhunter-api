package io.prhunter.api.bounty.api

import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class BountyView(
    val id: UUID,
    val repoId: Long,
    val repoOwner: String,
    val repoName: String,
    val issueId: Long,
    val issueNumber: Long,
    val firebaseUserId: String,
    val title: String,
    val problemStatement: String,
    val acceptanceCriteria: String,
    val languages: Array<String>,
    val tags: Array<String>,
    val experience: Experience,
    val bountyType: BountyType,
    val bountyValue: BigDecimal,
    val bountyValueUsd: BigDecimal,
    val bountyCurrency: String,
    val bountyStatus: BountyStatus,
    val createdAt: Instant,
    val expiresAt: Instant,
    val blockchainAddress: String?,
)

