package io.prhunter.api.bounty

import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import io.prhunter.api.common.errors.NotFoundException
import io.prhunter.api.crypto.CoinGeckoApiService
import io.prhunter.api.github.GithubService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.Issue
import io.prhunter.api.user.GithubUser
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BountyService(
    val bountyRepository: BountyRepository,
    val githubService: GithubService,
    val coinGeckoApiService: CoinGeckoApiService
) {

    fun createBounty(createBountyRequest: CreateBountyRequest, githubUser: GithubUser): Bounty {
        val repoData = getRepositoryAsUser(createBountyRequest.repoOwner, createBountyRequest.repoName, githubUser.accessToken)
        val issueData = getIssueAsUser(createBountyRequest.repoOwner, createBountyRequest.repoName, createBountyRequest.issueNumber, githubUser.accessToken)
        // TODO validate that this issue doesn't have a bounty active yet
        val bounty = Bounty(
            repoId = repoData.id,
            repoOwner = createBountyRequest.repoOwner,
            repoName = createBountyRequest.repoName,
            issueId = issueData.id,
            issueNumber = createBountyRequest.issueNumber,
            githubUserId = githubUser.id,
            title = createBountyRequest.title,
            body = createBountyRequest.body,
            bountyValue = createBountyRequest.bountyValue,
            bountyCurrency = createBountyRequest.bountyCurrency,
            languages = createBountyRequest.languages.toTypedArray(),
            tags = createBountyRequest.tags.toTypedArray(),
            experience = createBountyRequest.experience,
            bountyType = createBountyRequest.bountyType
        )
        return bountyRepository.save(bounty)
    }

    fun getBounty(id: Long): Bounty {
        return bountyRepository.findById(id).orElseThrow { NotFoundException(id) }
    }

    fun getUserBounties(githubUserId: Long): List<BountyView> {
        return bountyRepository.findByGithubUserId(githubUserId)
            .map { it.toView(coinGeckoApiService.getCurrentEthUsdPrice()) }
    }

    fun list(): List<Bounty> {
        return bountyRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"))
    }

    fun updateBounty(id: Long, updateBountyRequest: UpdateBountyRequest, githubUser: GithubUser): Bounty {
        val bounty = getBounty(id)
        if(githubUser.id != bounty.githubUserId){
            throw RepoAdminAccessRequired()
        }
        val updatedBounty = bounty.copy(
            body = updateBountyRequest.body,
            title = updateBountyRequest.title,
            languages = updateBountyRequest.languages.toTypedArray(),
            bountyCurrency = updateBountyRequest.bountryCurrency,
            bountyValue = updateBountyRequest.bountyValue,
            updatedAt = Instant.now(),
            tags = updateBountyRequest.tags.toTypedArray(),
            experience = updateBountyRequest.experience,
            bountyType = updateBountyRequest.bountyType
        )
        return bountyRepository.save(updatedBounty)
    }

    private fun getIssueAsUser(repoOwner: String, repoName: String, issueNumber: Long, accessToken: String): Issue {
        try{
            return githubService.getIssue(repoOwner, repoName, issueNumber, accessToken)
        }catch (ex: Throwable){
            throw RepoAdminAccessRequired()
        }

    }

    private fun getRepositoryAsUser(owner: String, repoName: String, userAccessToken: String): GHRepoData {
        // check if the user has admin access to the repository linked in the request
        try{
            return githubService.getRepository(owner, repoName, userAccessToken)
        }catch (ex: Throwable){
            throw RepoAdminAccessRequired()
//            val hasAdminAccess = userRepoList.find { it.id == repoId }?.permissions?.admin ?: false
        }
    }

}
