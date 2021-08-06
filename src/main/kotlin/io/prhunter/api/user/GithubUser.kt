package io.prhunter.api.user

import java.util.*

data class GithubUser(
    val id: UUID,
    val email: String,
    val accessToken: String
)