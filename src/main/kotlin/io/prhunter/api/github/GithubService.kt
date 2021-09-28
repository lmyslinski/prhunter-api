package io.prhunter.api.github

import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.GHRepoPermissionData
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Issue
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.GithubUser
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class GithubService(
    private val githubAppInstallationService: GithubAppInstallationService,
    private val installationService: InstallationService,
    val githubRestClient: GithubRestClient
) {

    fun listRepositories(currentUser: GithubUser): List<GHRepoData> {
        val installations = installationService.getInstallationsByUserId(currentUser.id)
        return if (installations.isNotEmpty()) {
            installations.map { installation ->
                runBlocking {
                    githubAppInstallationService.listRepositories(installation)
                }!!.repositories
            }.flatten()
        } else listOf()
    }

    fun listRepositoryIssues(owner: String, repo: String, token: String): List<Issue> {
        return runBlocking {
            githubRestClient.listIssues(owner, repo, token)
        }
    }

    // https://docs.github.com/en/rest/reference/repos#list-repositories-for-the-authenticated-user
    fun listAuthenticatedUserRepos(userToken: String): List<GHRepoPermissionData> {
        return runBlocking {
            githubRestClient.listAuthenticatedUserRepos(userToken)
        }
    }
}