package io.prhunter.api.config

import io.prhunter.api.oauth.GithubSecrets
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

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
                .redirectUri("http://localhost:8080/api/login/oauth2/code/github")
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