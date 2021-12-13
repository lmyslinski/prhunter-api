package io.prhunter.api.auth

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import io.prhunter.api.auth.AuthConstants.HEADER_STRING
import io.prhunter.api.auth.AuthConstants.TOKEN_PREFIX
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


private val log = KotlinLogging.logger {}

@Component
class TokenAuthenticationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(HEADER_STRING)
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            val authToken = header.replace(TOKEN_PREFIX, "")
            try {
                val defaultAuth = FirebaseAuth.getInstance()
                val token = defaultAuth.verifyIdToken(authToken)
                val authentication = UsernamePasswordAuthenticationToken(
                    token,
                    token,
                    listOf(SimpleGrantedAuthority("someRole"))
                )
                SecurityContextHolder.getContext().authentication = authentication
                log.info { "Stored firebase security context" }
            } catch (e: FirebaseAuthException) {
                log.error("firebase authentication has failed", e)
            } catch (e: Throwable) {
                log.warn("the token is expired and not valid anymore", e)
            }
        } else {
            log.debug("couldn't find bearer string, will ignore the header")
        }
        chain.doFilter(req, res)
    }
}

data class FirebaseUser(val id: String, val name: String, val pictureUrl: String)