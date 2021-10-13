package io.prhunter.api.bounty.api

import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import org.hibernate.annotations.Type
import java.math.BigDecimal
import java.time.Instant

data class BountyView(
    val id: Long,
    val repoId: Long,
    val issueId: Long,
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

