package io.prhunter.api.github

import io.prhunter.api.oauth.GithubSecrets
import org.kohsuke.github.GHApp
import org.kohsuke.github.GitHubBuilder
import org.springframework.stereotype.Service

@Service
class GithubAuthService(
    private val githubSecrets: GithubSecrets
) {

    private val githubAppClient by lazy {
        getApplicationGithubClient()
    }

    private fun getApplicationGithubClient(): GHApp {
        val tmpPrivateKey = GithubJwtService.generateTmpPrivateKey(githubSecrets.privateKey)
        val jwtToken = GithubJwtService.generateJwtKey(githubSecrets.appId, tmpPrivateKey)
        return GitHubBuilder().withJwtToken(jwtToken).build().app
    }

    fun getInstallationAuthToken(installationId: Long): String {
        val appInstallation = githubAppClient.getInstallationById(installationId)
        return appInstallation.createToken().create().token
    }

}