package io.prhunter.api.github

import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.common.errors.GithubAuthMissing
import io.prhunter.api.user.UserAccountService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Issue
import io.prhunter.api.installation.InstallationService
//import io.prhunter.api.user.GithubUser
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class GithubService(
    private val githubAppInstallationService: GithubAppInstallationService,
    private val installationService: InstallationService,
    private val userAccountService: UserAccountService,
    private val githubRestClient: GithubRestClient
) {

    fun listUserInstallationRepositories(currentUser: FirebaseUser): List<GHRepoData> {
        val userGithubId = userAccountService.getUserAccount(currentUser.id).githubUserId
        val installations = if(userGithubId != null){
            installationService.getInstallationsByUserId(userGithubId)
        }else listOf()
        return if (installations.isNotEmpty()) {
            installations.map { installation ->
                runBlocking {
                    githubAppInstallationService.listRepositories(installation.id)
                }!!.repositories
            }.flatten()
        } else listOf()
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