package io.prhunter.api.github.serverauth

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class GithubServerAuthInfo(
    @Id
    val id: String,
    val jwtToken: String,
    val lastUpdateTime: Instant
)