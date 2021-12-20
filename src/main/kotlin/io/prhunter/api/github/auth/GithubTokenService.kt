package io.prhunter.api.github.auth

import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.common.errors.GithubAuthMissing
import org.springframework.stereotype.Service

@Service
class GithubTokenService(
    private val githubUserRepository: GithubTokenRepository
) {

    fun getTokenForUser(user: FirebaseUser): String {
        return githubUserRepository.findByFirebaseUserId(user.id)?.accessToken
            ?: throw GithubAuthMissing()
    }

    fun getGithubUserId(user: FirebaseUser): Long {
        return githubUserRepository.findByFirebaseUserId(user.id)?.githubUserId
            ?: throw GithubAuthMissing()
    }
}