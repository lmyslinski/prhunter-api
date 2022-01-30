package io.prhunter.api.bounty

import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import io.prhunter.api.common.errors.BountyAlreadyExists
import io.prhunter.api.common.errors.IssueAdminAccessRequired
import io.prhunter.api.common.errors.NotFoundException
import io.prhunter.api.common.errors.RepoAdminAccessRequired
import io.prhunter.api.crypto.CoinGeckoApiService
import io.prhunter.api.crypto.CryptoCurrency
import io.prhunter.api.github.GithubService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.Issue
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

private val log = KotlinLogging.logger {}

@Service
class BountyService(
    val bountyRepository: BountyRepository,
    val githubService: GithubService,
    val coinGeckoApiService: CoinGeckoApiService
) {

    fun createBounty(createBountyRequest: CreateBountyRequest, user: FirebaseUser): BountyView {
        val repoData = getRepositoryAsUser(createBountyRequest.repoOwner, createBountyRequest.repoName, user)
        val issueData = getIssueAsUser(createBountyRequest.repoOwner, createBountyRequest.repoName, createBountyRequest.issueNumber, user)
        validateNoBountyFoundForIssue(issueData.id)
        val bounty = Bounty(
            repoId = repoData.id,
            repoOwner = createBountyRequest.repoOwner,
            repoName = createBountyRequest.repoName,
            issueId = issueData.id,
            issueNumber = createBountyRequest.issueNumber,
            firebaseUserId = user.id,
            title = createBountyRequest.title,
            problemStatement = createBountyRequest.problemStatement,
            acceptanceCriteria = createBountyRequest.acceptanceCriteria,
            bountyValue = createBountyRequest.bountyValue,
            bountyCurrency = createBountyRequest.bountyCurrency,
            languages = createBountyRequest.languages.toTypedArray(),
            tags = createBountyRequest.tags.toTypedArray(),
            experience = createBountyRequest.experience,
            bountyType = createBountyRequest.bountyType,
            bountyStatus = BountyStatus.PENDING,
            transactionHash = createBountyRequest.transactionHash,
        )
        return toView(bountyRepository.save(bounty))
    }

    fun getBounty(id: Long): Bounty {
        return bountyRepository.findById(id).orElseThrow { NotFoundException(id) }
    }

    fun getBountyView(id: Long): BountyView? {
        return toView(getBounty(id))
    }

    fun getBountyByIssueId(issueId: Long): Bounty? {
        return bountyRepository.findByIssueId(issueId)
    }

    fun getBountyViewByIssueId(issueId: Long): BountyView? {
        val bounty = bountyRepository.findByIssueId(issueId)
        return if(bounty != null){
             toView(bounty)
        }else null
    }

    fun getUserBounties(firebaseUserId: String): List<BountyView> {
        return bountyRepository.findByFirebaseUserId(firebaseUserId).map{toView(it)}
    }

    fun getFeaturedBounties(): List<BountyView> {
        return bountyRepository.findAll(PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "updatedAt"))).content.map { toView(it) }
    }

    fun list(): List<BountyView> {
        return bountyRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")).map { toView(it) }
    }

    fun updateBounty(id: Long, updateBountyRequest: UpdateBountyRequest, user: FirebaseUser): BountyView {
        val bounty = getBounty(id)
        if(user.id != bounty.firebaseUserId){
            throw RepoAdminAccessRequired()
        }
        val updatedBounty = bounty.copy(
            problemStatement = updateBountyRequest.problemStatement,
            acceptanceCriteria = updateBountyRequest.acceptanceCriteria,
            title = updateBountyRequest.title,
            languages = updateBountyRequest.languages.toTypedArray(),
            bountyCurrency = updateBountyRequest.bountryCurrency,
            bountyValue = updateBountyRequest.bountyValue,
            updatedAt = Instant.now(),
            tags = updateBountyRequest.tags.toTypedArray(),
            experience = updateBountyRequest.experience,
            bountyType = updateBountyRequest.bountyType
        )
        return toView(bountyRepository.save(updatedBounty))
    }

    fun getCompletedBy(firebaseUserId: String): List<BountyView> {
        return bountyRepository.findByCompletedBy(firebaseUserId).map { toView(it) }
    }

    private fun getIssueAsUser(repoOwner: String, repoName: String, issueNumber: Long, user: FirebaseUser): Issue {
        try{
            return githubService.getIssue(repoOwner, repoName, issueNumber, user)
        }catch (ex: Throwable){
            log.error( "Could not fetch issue ${repoOwner}/${repoName}/${issueNumber}", ex)
            throw IssueAdminAccessRequired()
        }

    }

    private fun getRepositoryAsUser(owner: String, repoName: String, user: FirebaseUser): GHRepoData {
        // check if the user has admin access to the repository linked in the request
        try{
            return githubService.getRepository(owner, repoName, user)
        }catch (ex: Throwable){
            log.error( "Could not fetch repository ${owner}/${repoName}", ex)
            throw RepoAdminAccessRequired()
//            val hasAdminAccess = userRepoList.find { it.id == repoId }?.permissions?.admin ?: false
        }
    }

    private fun validateNoBountyFoundForIssue(issueId: Long) {
        val existingBountyOpt = bountyRepository.findByIssueId(issueId)
        if(existingBountyOpt != null){
            log.error("Found a duplicate bounty for issue $issueId")
            throw BountyAlreadyExists()
        }
    }

    fun toView(bounty: Bounty): BountyView {
        val priceUSD = coinGeckoApiService.getCurrentPrice(CryptoCurrency.valueOf(bounty.bountyCurrency))
        return BountyView(
            bounty.id!!,
            bounty.repoId,
            bounty.repoOwner,
            bounty.repoName,
            bounty.issueId,
            bounty.issueNumber,
            bounty.firebaseUserId,
            bounty.title,
            bounty.problemStatement,
            bounty.acceptanceCriteria,
            bounty.languages,
            bounty.tags,
            bounty.experience,
            bounty.bountyType,
            bounty.bountyValue,
            bounty.bountyValue*priceUSD,
            bounty.bountyCurrency,
            bounty.bountyStatus,
            bounty.createdAt,
            bounty.updatedAt
        )
    }

    fun completeBounty(bounty: Bounty, firebaseUserId: String) {
        log.info { "Bounty was completed successfully" }
    }

}
