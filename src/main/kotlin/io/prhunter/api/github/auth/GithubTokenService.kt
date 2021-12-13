package io.prhunter.api.github.auth

import io.prhunter.api.auth.FirebaseUser
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class GithubTokenService(
    private val githubUserRepository: GithubTokenRepository
) {

    fun getTokenForUser(user: FirebaseUser): String {
        return githubUserRepository.findByFirebaseUserId(user.id)?.accessToken
            ?: throw RuntimeException("Github token not found for user ${user.id}")
    }
}