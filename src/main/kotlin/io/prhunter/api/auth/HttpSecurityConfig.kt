package io.prhunter.api.auth

import io.prhunter.api.github.GithubSecrets
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@EnableWebSecurity
@Configuration
class HttpSecurityConfig(
    private val githubSecrets: GithubSecrets,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val tokenAuthenticationFilter: TokenAuthenticationFilter
) : WebSecurityConfigurerAdapter() {

    private val customAuthorizedClientRepository = HttpSessionOAuth2AuthorizationRequestRepository()
    private val githubRequestModifierFilter =
        GithubRequestModifierFilter(customAuthorizedClientRepository, githubSecrets)

    override fun configure(http: HttpSecurity?) {
        http {
            authorizeRequests {
                authorize("/webhook", permitAll)
                authorize("/actuator/health/**", permitAll)
                authorize("/login/**", permitAll)
                authorize("/oauth2/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/swagger-ui/**", permitAll)
                authorize("/swagger-ui.html", permitAll)
                authorize("/bounty/search", permitAll)
                authorize("/contact", permitAll)
                authorize(HttpMethod.GET, "/bounty", permitAll)
                authorize(HttpMethod.GET, "/bounty/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            csrf {
                disable()
            }
            oauth2Login {
                defaultSuccessUrl(githubSecrets.successUrl, false)
                authorizationEndpoint {
                    authorizationRequestRepository = customAuthorizedClientRepository
                }
                authenticationSuccessHandler = oAuth2AuthenticationSuccessHandler

            }
            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            httpBasic {
                disable()
            }

            addFilterBefore(githubRequestModifierFilter, OAuth2LoginAuthenticationFilter::class.java)
            addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        }
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                        "https://prhunter.io"
                    )
                    .allowedMethods("*")
            }
        }
    }
}