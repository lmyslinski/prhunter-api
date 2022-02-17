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
class GithubUserService(
    private val userAccountService: UserAccountService,
    private val githubRestClient: GithubRestClient,
    private val githubAppService: GithubAppService,
    private val installationService: InstallationService
) {

    private val log = KotlinLogging.logger {}

    fun listUserInstallationRepositories(currentUser: FirebaseUser): List<GHRepoData> {
        val user = userAccountService.getUserAccount(currentUser.id)
        val installations = installationService.getUserInstallations(user)
        return installations.map { installation ->
            runBlocking {
                githubAppService.listRepositories(installation.id)
            }!!.repositories
        }.flatten()
    }

    fun listRepositoryIssues(owner: String, repo: String, user: FirebaseUser): List<Issue> {
        val token = userAccountService.getUserAccount(user.id).githubAccessToken ?: throw GithubAuthMissing()
        return runBlocking {
            githubRestClient.listIssues(owner, repo, token)
        }
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


}