package io.prhunter.api

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import io.prhunter.api.user.GithubUser
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

object TestDataProvider {

    val NOW = Instant.now()
    val TEST_USER = GithubUser(23L, "test-user", null, "Johny Cash", "tmp-token", Instant.now(), Instant.now())
    val BOUNTIES = listOf(
        Bounty(
            1L, 1L, "test-owner", "test-name", 1, 1, 1,"title", "body", arrayOf("scala"), tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature, BigDecimal.valueOf(10), "ETH", updatedAt = NOW.minus(
                1,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            2L, 2L, "test-owner", "test-name-2", 2, 2, 23,"title", "body", arrayOf("java"), tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature, BigDecimal.valueOf(20), "ETH", updatedAt = NOW.minus(
                2,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            3L, 2L, "test-owner", "test-name-3", 3, 3, 23,"title", "body",
            arrayOf("javascript"),
            tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature,
            BigDecimal.valueOf(30),
            "ETH",
            updatedAt = NOW.minus(
                3,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            4L, 2L, "test-owner", "test-name-4", 4, 4, 22,"title", "body",
            arrayOf("other"),
            tags = arrayOf("react", "ror"),
            Experience.Intermediate,
            BountyType.Meta,
            BigDecimal.valueOf(30),
            "ETH",
            updatedAt = NOW.minus(
                4,
                ChronoUnit.MINUTES
            )
        )
    )

}