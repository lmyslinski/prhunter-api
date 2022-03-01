package io.prhunter.api.github.serverauth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GithubServerAuthRepository : JpaRepository<GithubServerAuthInfo, String>{
    fun findById()
}