package io.prhunter.api.github.auth

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.bounty.fullName
import io.prhunter.api.github.serverauth.GithubAuthService
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.UserAccountService
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class AuthTokenResolver(
    private val installationService: InstallationService,
    private val userAccountService: UserAccountService,
    private val githubRestClient: GithubRestClient,
    private val githubAuthService: GithubAuthService,
) {

    // We don't care if we have multiple installations with access to the same repo, as long as we do have access to the repo
    internal fun getAccessTokenForBounty(bounty: Bounty): String {
        val user = userAccountService.getUserAccount(bounty.firebaseUserId)
        return installationService.getUserInstallations(user)
            .firstNotNullOfOrNull { getAuthTokenIfValidForBounty(it.id, bounty) }
            ?: throw RuntimeException("Access token for repository of bounty ${bounty.id} not found}")
    }

    private fun getAuthTokenIfValidForBounty(installationId: Long, bounty: Bounty): String? {
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return if (hasAccess(bounty, authToken)) {
            authToken
        } else null
    }

    private fun hasAccess(bounty: Bounty, authToken: String): Boolean {
        return runBlocking {
            val repos = githubRestClient.listRepositories(authToken).repositories.map { it.fullName }
            return@runBlocking repos.contains(bounty.fullName())
        }
    }
}