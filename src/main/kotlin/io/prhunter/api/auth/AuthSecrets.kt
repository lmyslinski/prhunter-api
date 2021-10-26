package io.prhunter.api.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuthSecrets(
    @Value("\${auth.jwtSecret}") val jwtSecret: String
)