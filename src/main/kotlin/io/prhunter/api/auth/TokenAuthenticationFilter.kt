package io.prhunter.api.auth

import io.jsonwebtoken.ExpiredJwtException
import io.prhunter.api.auth.Constants.HEADER_STRING
import io.prhunter.api.auth.Constants.TOKEN_PREFIX
import io.prhunter.api.oauth.UserService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.security.SignatureException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Component
class TokenAuthenticationFilter(
    @Autowired private val jwtTokenProvider: JwtTokenProvider,
    @Autowired private val userDetailsService: UserService
) : OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val header = req.getHeader(HEADER_STRING)
        var username: String? = null
        var authToken: String? = null
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "")
            try {
                username = jwtTokenProvider.getUsernameFromToken(authToken)
            } catch (e: IllegalArgumentException) {
                log.error("an error occured during getting username from token", e)
            } catch (e: ExpiredJwtException) {
                log.warn("the token is expired and not valid anymore", e)
            } catch (e: SignatureException) {
                log.error("Authentication Failed. Username or Password not valid.")
            }
        } else {
            log.debug("couldn't find bearer string, will ignore the header")
        }
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)
            if (jwtTokenProvider.validateToken(authToken, userDetails)) {
                val authentication: UsernamePasswordAuthenticationToken = jwtTokenProvider.getAuthentication(authToken, userDetails)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(req)
                log.debug("authenticated user $username, setting security context")
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        chain.doFilter(req, res)
    }

}