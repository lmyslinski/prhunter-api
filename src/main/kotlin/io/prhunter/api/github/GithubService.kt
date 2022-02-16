package io.prhunter.api.github

import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.Bounty
import io.prhunter.api.common.errors.GithubAuthMissing
import io.prhunter.api.user.UserAccountService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Issue
import io.prhunter.api.github.client.RepositoryList
import io.prhunter.api.installation.Installation
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.UserAccount
//import io.prhunter.api.user.GithubUser
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GithubService(
    private val githubAuthService: GithubAuthService,
    private val installationService: InstallationService,
    private val userAccountService: UserAccountService,
    private val githubRestClient: GithubRestClient,
    @Value("\${prhunter.frontendUrl}") val frontendUrl: String,
) {

    private val log = KotlinLogging.logger {}

    fun listUserInstallationRepositories(currentUser: FirebaseUser): List<GHRepoData> {
        val user = userAccountService.getUserAccount(currentUser.id)
        val installations = getUserInstallations(user)
        return installations.map { installation ->
            runBlocking {
                listRepositories(installation.id)
            }!!.repositories
        }.flatten()
    }

    suspend fun listRepositories(installationId: Long): RepositoryList? {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return githubRestClient.listRepositories(authToken)
    }

    fun listRepositoryIssues(owner: String, repo: String, user: FirebaseUser): List<Issue> {
        val token = userAccountService.getUserAccount(user.id).githubAccessToken ?: throw GithubAuthMissing()
        return runBlocking {
            githubRestClient.listIssues(owner, repo, token)
        }
    }

    suspend fun fetchIssue(issueUrl: String, installationId: Long): Issue{
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return githubRestClient.getIssueAtUrl(issueUrl, authToken)
    }

    fun getIssue(repoOwner: String, repoName: String, issueNumber: Long, user: FirebaseUser): Issue {
        val token = userAccountService.getUserAccount(user.id).githubAccessToken ?: throw GithubAuthMissing()
        return runBlocking {
            githubRestClient.getIssue(repoOwner, repoName, issueNumber, token)
        }
    }

    fun getRepository(owner: String, repo: String, user: FirebaseUser): GHRepoData {
        val token = userAccountService.getUserAccount(user.id).githubAccessToken ?: throw GithubAuthMissing()
        return runBlocking {
            githubRestClient.getRepository(owner, repo, token)
        }
    }


    fun newBountyComment(bounty: Bounty) {
        val user = userAccountService.getUserAccount(bounty.firebaseUserId)
        // get the app token so that we comment as the app and not the user
        val installations = getUserInstallations(user).filter { fetchToVerifyAccessToIssue(it.id, bounty) }
        when (installations.size) {
            0 -> log.error { "User has a new active bounty ${bounty.id} but there is no installation with access to the issue" }
            1 -> {
                val authToken = githubAuthService.getInstallationAuthToken(installations.first().id)
                addNewBountyComment(bounty, authToken)
            }
            else -> {
                val authToken = githubAuthService.getInstallationAuthToken(installations.first().id)
                addNewBountyComment(bounty, authToken)
                log.warn { "User ${user.firebaseUserId} has an unexpected amount of installations (${installations.size})" +
                        " with access to the issue of bounty ${bounty.id}" }
            }
        }
    }

    private fun fetchToVerifyAccessToIssue(installationId: Long, bounty: Bounty): Boolean {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return try{
            runBlocking {
                githubRestClient.getIssue(bounty.repoOwner, bounty.repoName, bounty.issueNumber, authToken)
            }
            true
        }catch(ex: Throwable){
            log.warn { "Could not fetch bounty ${bounty.id} issue with installation $installationId" }
            false
        }
    }

    private fun getUserInstallations(user: UserAccount): List<Installation> {
        val userGithubId = user.githubUserId
        return if (userGithubId != null) {
            installationService.getInstallationsByUserId(userGithubId)
        } else listOf()
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