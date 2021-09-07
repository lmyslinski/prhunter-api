package io.prhunter.api.github

import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.RepositoryList
import io.prhunter.api.installation.Installation
import org.springframework.stereotype.Service

@Service
class GithubAppInstallationService(
    val githubAuthService: GithubAuthService,
    val githubRestClient: GithubRestClient
) {

    suspend fun listRepositories(installation: Installation): RepositoryList? {
        val authToken = githubAuthService.getInstallationAuthToken(installation.id)
        return githubRestClient.listRepositories(authToken)
    }

}