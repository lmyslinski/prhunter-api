package io.prhunter.api.github

import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Issue
import io.prhunter.api.github.client.RepositoryList
import org.springframework.stereotype.Service

@Service
class GithubAppInstallationService(
    private val githubAuthService: GithubAuthService,
    private val githubRestClient: GithubRestClient
) {

    suspend fun listRepositories(installationId: Long): RepositoryList? {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return githubRestClient.listRepositories(authToken)
    }

    suspend fun fetchIssue(issueUrl: String, installationId: Long): Issue{
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return githubRestClient.getIssueAtUrl(issueUrl, authToken)
    }

}