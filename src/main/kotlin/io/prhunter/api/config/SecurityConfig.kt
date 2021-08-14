package io.prhunter.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke

@EnableWebSecurity
@Configuration
class OAuth2LoginSecurityConfig: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http{
            authorizeRequests {
                authorize("/webhook", permitAll)
                authorize("/login/**", permitAll)
                authorize("/oauth2/**", permitAll)
                authorize("/**", permitAll)
//                authorize(anyRequest, authenticated)
            }
            csrf {
                disable()
            }
            cors {
                disable()
            }
            oauth2Login {

            }
            httpBasic {
                disable()
            }
        }

//            .oauth2Login(withDefaults())
    }
//override fun configure(http: HttpSecurity?) {
//    http {
//        securityMatcher("/greetings/**")
//
//        httpBasic {}
//    }
//}
}