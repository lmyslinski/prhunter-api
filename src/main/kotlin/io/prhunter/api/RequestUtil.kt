package io.prhunter.api

import io.prhunter.api.user.GithubUser
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import java.security.Principal

object RequestUtil {
    fun getUserFromRequest(principal: Principal): GithubUser =
        ((principal as OAuth2AuthenticationToken).principal as GithubUser)
}