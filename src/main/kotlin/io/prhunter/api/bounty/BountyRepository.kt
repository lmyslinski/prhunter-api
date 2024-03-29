package io.prhunter.api.bounty

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface BountyRepository : JpaRepository<Bounty, UUID>{
    fun findByFirebaseUserId(firebaseUserId: String): List<Bounty>
    fun findByIssueId(issueId: Long): Bounty?
    fun findByIssueIdInAndBountyStatus(issuesId: List<Long>, status: BountyStatus): List<Bounty>
    fun findByIssueIdAndFirebaseUserIdAndBountyStatus(issueId: Long, firebaseUserId: String, bountyStatus: BountyStatus): Bounty?
    fun findAllByBountyStatusAndBountyCurrency(status: BountyStatus, bountyCurrency: String): List<Bounty>
    fun findAllByBountyStatusAndExpiresAtLessThan(status: BountyStatus, timestamp: Instant): List<Bounty>
    fun findByCompletedBy(firebaseUserId: String): List<Bounty>
    fun findByIssueIdAndBountyStatus(issueId: Long, active: BountyStatus): Bounty?
    fun findAllByBountyStatus(status: BountyStatus): List<Bounty>
}