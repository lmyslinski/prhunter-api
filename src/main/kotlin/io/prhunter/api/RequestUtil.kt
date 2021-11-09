package io.prhunter.api

import io.prhunter.api.user.GithubUser
import org.springframework.security.authentication.AbstractAuthenticationToken
import java.security.Principal

object RequestUtil {
    fun getUserFromRequest(principal: Principal): GithubUser =
        ((principal as AbstractAuthenticationToken).principal as GithubUser)
}