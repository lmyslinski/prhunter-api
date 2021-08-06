package io.prhunter.api.user

import java.io.Serializable
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class GithubUser(
    @Id
    var id: Long,
    var login: String,
    var email: String?,
    var name: String?,
    var accessToken: String,
    var githubRegisteredAt: Instant,
    var registeredAt: Instant = Instant.now()
): Serializable