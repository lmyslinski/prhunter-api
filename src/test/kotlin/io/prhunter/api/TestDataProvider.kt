package io.prhunter.api

import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.Bounty
import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

object TestDataProvider {

    val NOW = Instant.now()
    val TEST_USER = FirebaseUser("12345", "test-user", "pic-url")
    val BOUNTIES = listOf(
        Bounty(
            1L, 1L, "test-owner", "test-name", 1, 1, "12345","title", "statement", "acceptance",  arrayOf("scala"), tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature, BigDecimal.valueOf(10), "ETH", updatedAt = NOW.minus(
                1,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            2L, 2L, "test-owner", "test-name-2", 2, 2, "12345","title", "statement", "acceptance", arrayOf("java"), tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature, BigDecimal.valueOf(20), "ETH", updatedAt = NOW.minus(
                2,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            3L, 2L, "test-owner", "test-name-3", 3, 3, "12345","title", "statement", "acceptance",
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
            4L, 2L, "test-owner", "test-name-4", 4, 4, "12345","title", "statement", "acceptance",
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

    fun setAuthenticatedContext(user: FirebaseUser = TEST_USER){
        val authentication = UsernamePasswordAuthenticationToken(
            user,
            "",
            listOf(SimpleGrantedAuthority("user"))
        )
        SecurityContextHolder.getContext().authentication = authentication
    }

}