package io.prhunter.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val authorizedClientService: OAuth2AuthorizedClientService,
    private val authorizedClientRepository: OAuth2AuthorizedClientRepository
): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http{
            authorizeRequests {
                authorize("/webhook", permitAll)
                authorize("/login/**", permitAll)
                authorize("/oauth2/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            csrf {
                disable()
            }
            cors {
                disable()
            }
            oauth2Login {
                userInfoEndpoint {
                    userAuthoritiesMapper = userAuthoritiesMapper()
                }
                defaultSuccessUrl("http://localhost:3000/signup-success", false)

            }
            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            httpBasic {
                disable()
            }
            addFilterBefore(GithubRequestModifierFilter(), OAuth2LoginAuthenticationFilter::class.java)
        }
    }

    private fun userAuthoritiesMapper(): GrantedAuthoritiesMapper = GrantedAuthoritiesMapper { authorities: Collection<GrantedAuthority> ->
        val mappedAuthorities = emptySet<GrantedAuthority>()

        authorities.forEach { authority ->
            if (authority is OidcUserAuthority) {
                val idToken = authority.idToken
                val userInfo = authority.userInfo
                // Map the claims found in idToken and/or userInfo
                // to one or more GrantedAuthority's and add it to mappedAuthorities
            } else if (authority is OAuth2UserAuthority) {
                val userAttributes = authority.attributes
                // Map the attributes found in userAttributes
                // to one or more GrantedAuthority's and add it to mappedAuthorities
            }
        }

        mappedAuthorities
    }

//    private fun customGithubOAuthFilter(): GithubOAuth2LoginAuthenticationFilter {
//        return GithubOAuth2LoginAuthenticationFilter(clientRegistrationRepository, authorizedClientService, authorizedClientRepository)
//    }

//override fun configure(http: HttpSecurity?) {
//    http {
//        securityMatcher("/greetings/**")
//
//        httpBasic {}
//    }
//}
}