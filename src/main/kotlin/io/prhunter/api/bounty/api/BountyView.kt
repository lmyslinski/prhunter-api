package io.prhunter.api.bounty.api

import java.math.BigDecimal
import java.time.Instant

data class BountyView(
    val id: Long,
    val repoId: Long,
    val issueId: Long,
    val title: String,
    val body: String,
    val languages: List<String>,
    val bountyValue: BigDecimal,
    val bountyCurrency: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)