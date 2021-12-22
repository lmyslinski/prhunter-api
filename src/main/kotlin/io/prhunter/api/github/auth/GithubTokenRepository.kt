package io.prhunter.api.github.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GithubTokenRepository : JpaRepository<GithubToken, String> {
    fun findByFirebaseUserId(id: String): GithubToken?
    fun findByFirebaseUserIdOrGithubUserId(id: String, githubUserId: Long): GithubToken?
}