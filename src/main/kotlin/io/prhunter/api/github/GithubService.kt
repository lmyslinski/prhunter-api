package io.prhunter.api.github

import io.prhunter.api.github.client.GHRepoData
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

    fun listIssues(owner: String, repo: String, token: String): List<Issue> {
        return runBlocking {
            githubRestClient.listIssues(owner, repo, token)
        }
    }
}