package io.prhunter.api.auth

import io.prhunter.api.github.GithubSecrets
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

// When the oauth flow is triggered during Github App installation, the `state` variable and the authorization
// context are both missing. To make the Spring Security Github handling work, we need to add the `state variable to the
// URL as well as add some mock authorization request to the repository matched with that state. This filter wraps
// an incoming request and does just that.
class GithubRequestModifierFilter(
    private val oauthRequestRepository: AuthorizationRequestRepository<OAuth2AuthorizationRequest>,
    private val githubSecrets: GithubSecrets
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrapperRequest = GithubRequestWrapper(request, response, oauthRequestRepository, githubSecrets)
        filterChain.doFilter(wrapperRequest, response)
    }
}

class GithubRequestWrapper(
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
    private val oauthRequestRepository: AuthorizationRequestRepository<OAuth2AuthorizationRequest>,
    private val githubSecrets: GithubSecrets
) : HttpServletRequestWrapper(request) {

    private val newParamMap = initParameterMap()

    private fun initParameterMap(): MutableMap<String, Array<String>> {
        val originalParamMap = this.request.parameterMap.toMutableMap()
        if (originalParamMap.containsKey("setup_action")) {
            originalParamMap.putIfAbsent("state", arrayOf("random-state"))
            val mockOAuth2Request = OAuth2AuthorizationRequest.authorizationCode().authorizationRequestUri(
                "https://github.com/login/oauth/authorize",
            ).clientId(githubSecrets.clientId)
                .redirectUri(githubSecrets.redirectUrl)
                .state("random-state")
                .scope("read:user")
                .authorizationRequestUri("doesn't matter at this stage")
                .authorizationUri("https://github.com/login/oauth/authorize")
                .attributes(
                    mapOf(
                        "registration_id" to "github",
                        "org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository.AUTHORIZATION_REQUEST" to this
                    )
                ).build()
            oauthRequestRepository.saveAuthorizationRequest(mockOAuth2Request, request, response)
        }
        return originalParamMap
    }

    override fun getParameter(name: String?): String? {
        return newParamMap[name!!]?.firstOrNull()
    }

    override fun getParameterValues(name: String?): Array<String> {
        return newParamMap[name!!] ?: arrayOf()
    }

    override fun getParameterMap(): MutableMap<String, Array<String>> {
        return newParamMap
    }
}