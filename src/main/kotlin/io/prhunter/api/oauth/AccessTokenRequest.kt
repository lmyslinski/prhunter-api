package io.prhunter.api.oauth

data class AccessTokenRequest(
    val code: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)