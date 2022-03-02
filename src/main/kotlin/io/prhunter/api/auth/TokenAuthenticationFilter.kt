package io.prhunter.api.auth

import com.google.firebase.auth.FirebaseAuthException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Component
class TokenAuthenticationFilter(private val firebaseService: FirebaseService) : OncePerRequestFilter() {

    private val TOKEN_PREFIX = "Bearer "
    private val HEADER_STRING = "Authorization"

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(HEADER_STRING)
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            val authToken = header.replace(TOKEN_PREFIX, "")
            try {
                firebaseService.signInWithFirebase(authToken)
            } catch (e: FirebaseAuthException) {
                log.warn { "Received header: $header on a request: ${req.requestURI}" }
                log.warn("firebase authentication has failed", e)
            } catch (e: Throwable) {
                log.warn("the token is expired and not valid anymore", e)
            }
        } else {
            log.trace("couldn't find bearer string, will ignore the header")
        }
        chain.doFilter(req, res)
    }
}

