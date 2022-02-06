package io.prhunter.api.bounty

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BountyRepository : JpaRepository<Bounty, UUID>{
    fun findByFirebaseUserId(firebaseUserId: String): List<Bounty>
    fun findByIssueId(issueId: Long): Bounty?
    fun findByIssueNumber(issueNumber: Long): Bounty?
    fun findAllByBountyStatus(status: BountyStatus): List<Bounty>
    fun findByCompletedBy(firebaseUserId: String): List<Bounty>
}