package io.prhunter.api.auth

object AuthConstants {
    // const val ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60.toLong()
    // set to 4 weeks for easier dev
    const val ACCESS_TOKEN_VALIDITY_SECONDS = 2419200.toLong()
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
    const val AUTHORITIES_KEY = "scopes"
}