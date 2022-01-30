package io.prhunter.api.github.auth

import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.common.errors.GithubAuthMissing
import io.prhunter.api.user.UserAccountRepository
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val userAccountRepository: UserAccountRepository
) {

    fun getTokenForUser(user: FirebaseUser): String {
        return userAccountRepository.findByFirebaseUserId(user.id)?.githubAccessToken
            ?: throw GithubAuthMissing()
    }

    fun getGithubUserId(user: FirebaseUser): Long {
        return userAccountRepository.findByFirebaseUserId(user.id)?.githubUserId
            ?: throw GithubAuthMissing()
    }
}