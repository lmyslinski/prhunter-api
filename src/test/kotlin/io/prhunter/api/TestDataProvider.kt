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
import java.util.*

object TestDataProvider {

    val NOW = Instant.now()
    val TEST_USER = FirebaseUser("12345", "test-user", "pic-url")
    val BOUNTIES = listOf(
        Bounty(
            UUID.fromString("2d8ab1a6-dfc3-4dc5-b476-5d7d4f69f9dc"), 1L, "test-owner", "test-name", 1, 1, "12345","title", "statement", "acceptance",  arrayOf("scala"), tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature, BigDecimal.valueOf(10), BigDecimal.valueOf(10),"ETH", createdAt = NOW.minus(
                1,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            UUID.fromString("ff6e2ea5-5970-4168-8028-78dfb42b780f"), 2L, "test-owner", "test-name-2", 2, 2, "12345","title-2", "statement", "title-4", arrayOf("java"), tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature, BigDecimal.valueOf(20), BigDecimal.valueOf(20),"ETH", createdAt = NOW.minus(
                10,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            UUID.fromString("ce05f3f7-0f9c-4fec-86c6-ddb203125f6d"), 2L, "test-owner", "test-name-3", 3, 3, "12345","title-3", "title-4", "acceptance",
            arrayOf("javascript"),
            tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Feature,
            BigDecimal.valueOf(30),
            BigDecimal.valueOf(30),
            "ETH",
            createdAt = NOW.minus(
                5,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            UUID.fromString("8fe5bc6a-7e24-4153-9a27-88596ab98902"), 2L, "test-owner", "test-name-4", 4, 4, "12345","title-4", "statement", "acceptance",
            arrayOf("other"),
            tags = arrayOf("react", "ror"),
            Experience.Intermediate,
            BountyType.Meta,
            BigDecimal.valueOf(30),
            BigDecimal.valueOf(30),
            "ETH",
            createdAt = NOW.minus(
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