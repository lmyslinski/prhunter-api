package io.prhunter.api.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GithubUserRepository : JpaRepository<GithubUser, Long> {
}