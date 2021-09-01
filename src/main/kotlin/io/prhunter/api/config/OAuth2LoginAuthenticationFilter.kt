package io.prhunter.api.config

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.web.util.UrlUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class GithubOAuth2LoginAuthenticationFilter(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val authorizedClientRepository: OAuth2AuthorizedClientRepository
) : OAuth2LoginAuthenticationFilter(clientRegistrationRepository, authorizedClientService) {

    private val authorizationRequestRepository: AuthorizationRequestRepository<OAuth2AuthorizationRequest> =
        HttpSessionOAuth2AuthorizationRequestRepository()



    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val params = toMultiMap(request.parameterMap)
        if (!isAuthorizationResponse(params)) {
            val oauth2Error = OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        val authorizationRequest = authorizationRequestRepository.removeAuthorizationRequest(request, response)
        val requestWithState = request.parameterMap
//        if (authorizationRequest == null) {
//            val oauth2Error = OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE)
//            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
//        }
        val registrationId = authorizationRequest.getAttribute<String>(OAuth2ParameterNames.REGISTRATION_ID)
        val clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId)
        if (clientRegistration == null) {
            val oauth2Error = OAuth2Error(
                "client_registration_not_found",
                "Client Registration not found with Id: $registrationId", null
            )
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        // @formatter:off
        // @formatter:off
        val redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
            .replaceQuery(null)
            .build()
            .toUriString()
        // @formatter:on
        // @formatter:on
        val authorizationResponse = convert(
            params,
            redirectUri
        )
        val authenticationDetails = authenticationDetailsSource.buildDetails(request)
        val authenticationRequest = OAuth2LoginAuthenticationToken(
            clientRegistration,
            OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse)
        )
        authenticationRequest.details = authenticationDetails
        val authenticationResult =
            authenticationManager.authenticate(authenticationRequest) as OAuth2LoginAuthenticationToken
        val oauth2Authentication = OAuth2AuthenticationToken(
            authenticationResult.principal, authenticationResult.authorities,
            authenticationResult.clientRegistration.registrationId
        )
        oauth2Authentication.details = authenticationDetails
        val authorizedClient = OAuth2AuthorizedClient(
            authenticationResult.clientRegistration, oauth2Authentication.name,
            authenticationResult.accessToken, authenticationResult.refreshToken
        )

        authorizedClientRepository.saveAuthorizedClient(authorizedClient, oauth2Authentication, request, response)
        return oauth2Authentication
    }

    fun convert(request: MultiValueMap<String, String>, redirectUri: String): OAuth2AuthorizationResponse? {
        val code = request.getFirst(OAuth2ParameterNames.CODE)
        val errorCode = request.getFirst(OAuth2ParameterNames.ERROR)
        val state = request.getFirst(OAuth2ParameterNames.STATE)
        if (StringUtils.hasText(code)) {
            return OAuth2AuthorizationResponse.success(code).redirectUri(redirectUri).state(state).build()
        }
        val errorDescription = request.getFirst(OAuth2ParameterNames.ERROR_DESCRIPTION)
        val errorUri = request.getFirst(OAuth2ParameterNames.ERROR_URI)
        // @formatter:off
        return OAuth2AuthorizationResponse.error(errorCode)
            .redirectUri(redirectUri)
            .errorDescription(errorDescription)
            .errorUri(errorUri)
            .state(state)
            .build()
        // @formatter:on
    }

    fun toMultiMap(map: Map<String, Array<String?>>): MultiValueMap<String, String> {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap(map.size)
        map.forEach { (key: String, values: Array<String?>) ->
            if (values.isNotEmpty()) {
                for (value in values) {
                    params.add(key, value)
                }
            }
        }
        return params
    }

    fun isAuthorizationResponse(request: MultiValueMap<String, String>): Boolean {
        return isAuthorizationResponseSuccess(request) || isAuthorizationResponseError(
            request
        )
    }

    fun isAuthorizationResponseSuccess(request: MultiValueMap<String, String>): Boolean {
        return (StringUtils.hasText(request.getFirst(OAuth2ParameterNames.CODE)))
    }

    fun isAuthorizationResponseError(request: MultiValueMap<String, String>): Boolean {
        return (StringUtils.hasText(request.getFirst(OAuth2ParameterNames.ERROR)))
    }
}