package io.prhunter.api.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAccountRepository : JpaRepository<UserAccount, String> {
    fun findByFirebaseUserId(id: String): UserAccount?
    fun findByGithubUserId(githubUserId: Long): UserAccount?
    fun findByFirebaseUserIdOrGithubUserId(id: String, githubUserId: Long): UserAccount?
}