package io.prhunter.api.github

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Issue
import io.prhunter.api.github.client.RepositoryList
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.UserAccountService
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GithubAppService(
    private val githubAuthService: GithubAuthService,
    private val installationService: InstallationService,
    private val userAccountService: UserAccountService,
    private val githubRestClient: GithubRestClient,
    @Value("\${prhunter.frontendUrl}") val frontendUrl: String,
) {

    private val log = KotlinLogging.logger {}

    fun newBountyComment(bounty: Bounty) {
        val user = userAccountService.getUserAccount(bounty.firebaseUserId)
        // get the app token so that we comment as the app and not the user
        val installationTokensWithAccess = installationService.getUserInstallations(user).mapNotNull { getAuthTokenIfValidForBounty(it.id, bounty) }
        when (installationTokensWithAccess.size) {
            0 -> log.error { "User has a new active bounty ${bounty.id} but there is no installation with access to the issue" }
            1 -> {
                addNewBountyComment(bounty, installationTokensWithAccess.first())
            }
            else -> {
                addNewBountyComment(bounty, installationTokensWithAccess.first())
                log.warn { "User ${user.firebaseUserId} has an unexpected amount of installations (${installationTokensWithAccess.size})" +
                        " with access to the issue of bounty ${bounty.id}" }
            }
        }
    }

    suspend fun listRepositories(installationId: Long): RepositoryList? {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return githubRestClient.listRepositories(authToken)
    }


    suspend fun fetchIssue(issueUrl: String, installationId: Long): Issue {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return githubRestClient.getIssueAtUrl(issueUrl, authToken)
    }

    private fun getAuthTokenIfValidForBounty(installationId: Long, bounty: Bounty): String? {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return try{
            runBlocking {
                githubRestClient.getIssue(bounty.repoOwner, bounty.repoName, bounty.issueNumber, authToken)
            }
            authToken
        }catch(ex: Throwable){
            log.warn { "Could not fetch bounty ${bounty.id} issue with installation $installationId" }
            null
        }
    }

    private fun addNewBountyComment(bounty: Bounty, authToken: String) {
        runBlocking {
            githubRestClient.postIssueComment(bounty.repoOwner, bounty.repoName, bounty.issueNumber, getNewCommentBody(bounty), authToken)
        }
    }

    private fun getNewCommentBody(bounty: Bounty): String =
        "A new [PRHunter bounty]($frontendUrl/bounties/${bounty.id}) was just created for this issue! " +
                "The author offers ${bounty.bountyValue} ${bounty.bountyCurrency} (~\$${bounty.bountyValueUsd}) for solving it. " +
                "Submit a pull request to claim your reward. More details [here]($frontendUrl/docs#completing-bounties)."
}