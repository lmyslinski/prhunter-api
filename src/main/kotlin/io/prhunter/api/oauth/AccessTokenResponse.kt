package io.prhunter.api.oauth

data class AccessTokenResponse(
    val accessToken: String,
    val tokenType: String,
    val scope: String
)
