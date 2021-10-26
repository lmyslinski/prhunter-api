package io.prhunter.api.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.prhunter.api.auth.AuthConstants.ACCESS_TOKEN_VALIDITY_SECONDS
import io.prhunter.api.auth.AuthConstants.AUTHORITIES_KEY
import io.prhunter.api.user.GithubUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KFunction1

@Service
class JwtTokenProvider(@Autowired private val authSecrets: AuthSecrets) {

    private val secretKey = Keys.hmacShaKeyFor(authSecrets.jwtSecret.toByteArray())

    fun generateToken(authentication: Authentication): String {
        val authorities: String = (authentication.principal as GithubUser)
            .authorities.stream().map { it.toString() }
            .collect(Collectors.joining(","))
        return Jwts.builder()
            .setSubject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .compact()
    }

    fun getAuthentication(token: String?, userDetails: UserDetails?): UsernamePasswordAuthenticationToken {
        val jwtParser: JwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build()
        val claimsJws: Jws<*> = jwtParser.parseClaimsJws(token)
        val claims: Claims = claimsJws.body as Claims
        val authorities = (claims[AUTHORITIES_KEY]).toString()
            .split(",".toRegex()).toTypedArray().filter { it.isNotEmpty() }
            .map { SimpleGrantedAuthority(it) }
        return UsernamePasswordAuthenticationToken(userDetails, "", authorities)
    }

    fun getUsernameFromToken(token: String?): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    private fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token, Claims::getExpiration)
    }

    private fun <T> getClaimFromToken(token: String?, claimsResolver: KFunction1<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.call(claims)
    }

    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts
            .parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String?): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}