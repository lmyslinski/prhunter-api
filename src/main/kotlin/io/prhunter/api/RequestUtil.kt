package io.prhunter.api

import io.prhunter.api.auth.FirebaseUser
import org.springframework.security.authentication.AbstractAuthenticationToken
import java.security.Principal

object RequestUtil {
    fun getUserFromRequest(principal: Principal): FirebaseUser =
        ((principal as AbstractAuthenticationToken).principal as FirebaseUser)
}