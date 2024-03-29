package io.prhunter.api.bounty

import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.CreateBountyResponse
import io.prhunter.api.common.errors.BountyAlreadyExists
import io.prhunter.api.common.errors.IssueAdminAccessRequired
import io.prhunter.api.common.errors.NotFoundException
import io.prhunter.api.common.errors.RepoAdminAccessRequired
import io.prhunter.api.crypto.CoinGeckoApiService
import io.prhunter.api.crypto.CryptoCurrency
import io.prhunter.api.crypto.CryptoResolver
import io.prhunter.api.github.GithubUserService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.Issue
import io.prhunter.api.user.UserAccount
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class BountyService(
    private val bountyRepository: BountyRepository,
    private val githubUserService: GithubUserService,
    private val coinGeckoApiService: CoinGeckoApiService,
    private val cryptoResolver: CryptoResolver
) {

    // In order to solve the problem with multiple submissions and account for the user cancelling the metamask tx we accept this payload multiple times
    fun getOrCreateBounty(createBountyRequest: CreateBountyRequest, user: FirebaseUser): CreateBountyResponse {
        val issueData = getIssueAsUser(createBountyRequest.repoOwner, createBountyRequest.repoName, createBountyRequest.issueNumber, user)
        val bountyOpt = bountyRepository.findByIssueIdAndFirebaseUserIdAndBountyStatus(issueData.id, user.id, BountyStatus.PENDING)
        val contractService = cryptoResolver.getContractService(CryptoCurrency.valueOf(createBountyRequest.bountyCurrency))
        return if(bountyOpt != null){
            log.info { "Bounty already exists, returning the existing entity" }
            CreateBountyResponse(bountyOpt.id!!, contractService.getBountyFactoryAddress())
        }else{
            validateNoBountyFoundForIssue(issueData.id)
            createBounty(createBountyRequest, issueData.id, user)
        }
    }

    fun createBounty(createBountyRequest: CreateBountyRequest, issueId: Long, user: FirebaseUser): CreateBountyResponse {
        val repoData = getRepositoryAsUser(createBountyRequest.repoOwner, createBountyRequest.repoName, user)
        val currentCryptoPrice = coinGeckoApiService.getCurrentPrice(CryptoCurrency.valueOf(createBountyRequest.bountyCurrency))
        val bountyValueUsd = createBountyRequest.bountyValue.multiply(currentCryptoPrice)
        val contractService = cryptoResolver.getContractService(CryptoCurrency.valueOf(createBountyRequest.bountyCurrency))
        val bounty = Bounty(
            repoId = repoData.id,
            repoOwner = createBountyRequest.repoOwner,
            repoName = createBountyRequest.repoName,
            issueId = issueId,
            issueNumber = createBountyRequest.issueNumber,
            firebaseUserId = user.id,
            title = createBountyRequest.title,
            problemStatement = createBountyRequest.problemStatement,
            acceptanceCriteria = createBountyRequest.acceptanceCriteria,
            bountyValue = createBountyRequest.bountyValue,
            bountyValueUsd = bountyValueUsd,
            bountyCurrency = createBountyRequest.bountyCurrency,
            languages = createBountyRequest.languages.toTypedArray(),
            tags = createBountyRequest.tags.toTypedArray(),
            experience = createBountyRequest.experience,
            bountyType = createBountyRequest.bountyType,
            bountyStatus = BountyStatus.PENDING,
            expiresAt = Instant.ofEpochSecond(createBountyRequest.expiresAt)
        )
        val newBounty = toView(bountyRepository.save(bounty))
        log.info { "Created a new pending bounty: ${newBounty.id}" }
        return CreateBountyResponse(newBounty.id, contractService.getBountyFactoryAddress())
    }

    fun getBounty(id: UUID): Bounty {
        return bountyRepository.findById(id).orElseThrow { NotFoundException(id) }
    }

    fun getBountyView(id: UUID): BountyView? {
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
        return bountyRepository.findAll(PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "createdAt"))).content.map { toView(it) }
    }

    fun list(): List<BountyView> {
        return bountyRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).map { toView(it) }
    }

    fun getCompletedBy(firebaseUserId: String): List<BountyView> {
        return bountyRepository.findByCompletedBy(firebaseUserId).map { toView(it) }
    }

    private fun getIssueAsUser(repoOwner: String, repoName: String, issueNumber: Long, user: FirebaseUser): Issue {
        try{
            return githubUserService.getIssue(repoOwner, repoName, issueNumber, user)
        }catch (ex: Throwable){
            log.error( "Could not fetch issue ${repoOwner}/${repoName}/${issueNumber}", ex)
            throw IssueAdminAccessRequired()
        }
    }

    private fun getRepositoryAsUser(owner: String, repoName: String, user: FirebaseUser): GHRepoData {
        // check if the user has admin access to the repository linked in the request
        try{
            return githubUserService.getRepository(owner, repoName, user)
        }catch (ex: Throwable){
            log.error( "Could not fetch repository ${owner}/${repoName}", ex)
            throw RepoAdminAccessRequired()
//            val hasAdminAccess = userRepoList.find { it.id == repoId }?.permissions?.admin ?: false
        }
    }

    private fun validateNoBountyFoundForIssue(issueId: Long) {
        val existingBountyOpt = bountyRepository.findByIssueIdAndBountyStatus(issueId, BountyStatus.ACTIVE)
        if(existingBountyOpt != null){
            log.error("Found a duplicate bounty for issue $issueId")
            throw BountyAlreadyExists()
        }
    }

    fun toView(bounty: Bounty): BountyView {
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
            bounty.bountyValueUsd,
            bounty.bountyCurrency,
            bounty.bountyStatus,
            bounty.createdAt,
            bounty.expiresAt,
            bounty.blockchainAddress,
            cryptoResolver.getBlockchainUrl(bounty.blockchainAddress, CryptoCurrency.valueOf(bounty.bountyCurrency))
        )
    }

    fun completeBounty(bounty: Bounty, user: UserAccount) {
        val contractService = cryptoResolver.getContractService(CryptoCurrency.valueOf(bounty.bountyCurrency))
        contractService.payoutBounty(user.ethWalletAddress!!, bounty)
        bounty.bountyStatus = BountyStatus.COMPLETED
        bounty.completedBy = user.firebaseUserId
        bounty.completedAt = Instant.now()
        bountyRepository.save(bounty)

        // Missing steps to make this actually work:
        // Add webhook signature signing verification so that we can't just get someone to send us a request
        log.info { "Bounty was completed successfully" }
    }

    fun cancelBounty(bounty: Bounty, reason: String) {
        bounty.bountyStatus = BountyStatus.CANCELLED
        bountyRepository.save(bounty)
        log.info { "Bounty ${bounty.id} was cancelled due to: $reason" }
    }

    fun getByStatus(status: BountyStatus): List<Bounty> {
        return bountyRepository.findAllByBountyStatus(status)
    }
}
