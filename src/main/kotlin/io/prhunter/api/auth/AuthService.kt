package io.prhunter.api.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthService {

    private val BASIC_ROLE: String = "user"

    fun signInWithFirebase(authToken: String) {
        val defaultAuth = FirebaseAuth.getInstance()
        val token = defaultAuth.verifyIdToken(authToken)
        val authentication = UsernamePasswordAuthenticationToken(
            token.toUser(),
            authToken,
            listOf(SimpleGrantedAuthority(BASIC_ROLE))
        )
        SecurityContextHolder.getContext().authentication = authentication
    }

    fun getUserById(userId: String): UserRecord? {
        val defaultAuth = FirebaseAuth.getInstance()
        return defaultAuth.getUser(userId)
    }
}