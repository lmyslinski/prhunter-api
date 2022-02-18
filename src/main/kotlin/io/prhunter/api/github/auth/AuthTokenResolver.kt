package io.prhunter.api.github.auth

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.github.GithubAuthService
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.UserAccountService
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AuthTokenResolver(
    private val installationService: InstallationService,
    private val userAccountService: UserAccountService,
    private val githubRestClient: GithubRestClient,
    private val githubAuthService: GithubAuthService,
) {

    private val log = KotlinLogging.logger {}

    // We dont care if we have multiple installations with access to the same repo, as long as we do have access to the repo
    internal fun getAccessTokenForBounty(bounty: Bounty): String {
        val user = userAccountService.getUserAccount(bounty.firebaseUserId)
        return installationService.getUserInstallations(user)
            .firstNotNullOfOrNull { getAuthTokenIfValidForBounty(it.id, bounty) }
            ?: throw RuntimeException("Access token for repository of bounty ${bounty.id} not found}")
    }

    private fun getAuthTokenIfValidForBounty(installationId: Long, bounty: Bounty): String? {
        // TODO this doesn't work with fetching the issue as issues are public after all
        // Needs a bit more secure call
        // let's list repositories instead and verify the bounty repo is on the list
        val authToken = githubAuthService.getInstallationAuthToken(installationId)
        return try {
            runBlocking {
                githubRestClient.getIssue(bounty.repoOwner, bounty.repoName, bounty.issueNumber, authToken)
            }
            authToken
        } catch (ex: Throwable) {
            log.warn { "Could not fetch bounty ${bounty.id} issue with installation $installationId" }
            null
        }
    }
}