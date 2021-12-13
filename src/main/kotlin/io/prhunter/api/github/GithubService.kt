package io.prhunter.api.github

import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.GHRepoPermissionData
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
    private val githubRestClient: GithubRestClient
) {

    fun listUserInstallationRepositories(currentUser: String): List<GHRepoData> {
        val installations = installationService.getInstallationsByUserId(0L)
        return if (installations.isNotEmpty()) {
            installations.map { installation ->
                runBlocking {
                    githubAppInstallationService.listRepositories(installation.id)
                }!!.repositories
            }.flatten()
        } else listOf()
    }

    fun listRepositoryIssues(owner: String, repo: String, token: String): List<Issue> {
        return runBlocking {
            githubRestClient.listIssues(owner, repo, token)
        }
    }

    fun getIssue(repoOwner: String, repoName: String, issueNumber: Long, accessToken: String): Issue {
        return runBlocking {
            githubRestClient.getIssue(repoOwner, repoName, issueNumber, accessToken)
        }
    }

    fun getRepository(owner: String, repo: String, token: String): GHRepoData {
        return runBlocking {
            githubRestClient.getRepository(owner, repo, token)
        }
    }

    // https://docs.github.com/en/rest/reference/repos#list-repositories-for-the-authenticated-user
    fun listAuthenticatedUserRepos(userToken: String): List<GHRepoPermissionData> {
        return runBlocking {
            githubRestClient.listAuthenticatedUserRepos(userToken)
        }
    }
}