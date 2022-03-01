package io.prhunter.api.github

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.github.auth.AuthTokenResolver
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.RepositoryList
import io.prhunter.api.github.serverauth.GithubAuthService
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GithubAppService(
    private val githubAuthService: GithubAuthService,
    private val githubRestClient: GithubRestClient,
    private val authTokenResolver: AuthTokenResolver,
    @Value("\${prhunter.frontendUrl}") val frontendUrl: String,
) {

    fun newPullRequestComment(bounty: Bounty, prNumber: Long, profileUrl: String) {
        val installationBountyAccessToken = authTokenResolver.getAccessTokenForBounty(bounty)
        runBlocking {
            githubRestClient.postPullRequestComment(
                bounty.repoOwner,
                bounty.repoName,
                prNumber,
                getNewPullRequestCommentBody(bounty, profileUrl),
                installationBountyAccessToken
            )
        }
    }

    fun newBountyComment(bounty: Bounty) {
        val installationBountyAccessToken = authTokenResolver.getAccessTokenForBounty(bounty)
        runBlocking {
            githubRestClient.postIssueComment(
                bounty.repoOwner,
                bounty.repoName,
                bounty.issueNumber,
                getNewBountyCommentBody(bounty),
                installationBountyAccessToken
            )
        }
    }

    fun expireBountyComment(bounty: Bounty) {
        val installationBountyAccessToken = authTokenResolver.getAccessTokenForBounty(bounty)
        runBlocking {
            githubRestClient.postIssueComment(
                bounty.repoOwner,
                bounty.repoName,
                bounty.issueNumber,
                getExpireBountyCommentBody(bounty),
                installationBountyAccessToken
            )
        }
    }

    suspend fun listRepositories(installationId: Long): RepositoryList? {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return githubRestClient.listRepositories(authToken)
    }


    private fun getNewBountyCommentBody(bounty: Bounty): String =
        "A new [PRHunter bounty]($frontendUrl/bounties/${bounty.id}) was just created for this issue! " +
                "The author offers ${bounty.bountyValue} ${bounty.bountyCurrency} (~\$${bounty.bountyValueUsd}) for solving it. " +
                "Submit a pull request to claim your reward. More details [here]($frontendUrl/docs#completing-bounties)."

    private fun getExpireBountyCommentBody(bounty: Bounty): String = "The [PRHunter bounty]($frontendUrl/bounties/${bounty.id}) has expired and is no longer active. The bounty value was returned to the bounty owner."

    private fun getNewPullRequestCommentBody(bounty: Bounty, profileUrl: String): String {
        val name = profileUrl.split('/').last()
        return "This PR is a valid candidate for this [PRHunter bounty]($frontendUrl/bounties/${bounty.id}). Once it's merged by the repo owner, the reward of ${bounty.bountyValue} ${bounty.bountyCurrency} (~\$${bounty.bountyValueUsd}) will go to [@$name]($profileUrl)!"
    }
}