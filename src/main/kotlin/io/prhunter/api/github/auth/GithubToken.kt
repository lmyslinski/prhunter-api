package io.prhunter.api.github.auth

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class GithubToken(
    @Id
    var firebaseUserId: String,
    var accessToken: String
)