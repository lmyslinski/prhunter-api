package io.prhunter.api.github

import mu.KotlinLogging
import org.kohsuke.github.GHApp
import org.kohsuke.github.GitHubBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

private val log = KotlinLogging.logger {}

@Service
@Profile("!test")
class GithubAuthServiceImpl(
    private val githubSecrets: GithubSecrets
) : GithubAuthService {

    private var ghApp: GHApp? = null
    private var lastRefreshTime: Instant = Instant.MIN

    // Functional programming ftw
    private fun refreshJwtTokenIfStale(){
        // if more than 5 minutes have passed since last jwt token refresh
        if(Instant.now().minus(5, ChronoUnit.MINUTES).isAfter(lastRefreshTime)){
            getApplicationGithubClient()
            lastRefreshTime = Instant.now()
        }
    }

    private fun getApplicationGithubClient() {
        log.info { "Refreshing application JWT key" }
        val tmpPrivateKey = GithubJwtService.generateTmpPrivateKey(githubSecrets.privateKey)
        val jwtToken = GithubJwtService.generateJwtKey(githubSecrets.appId, tmpPrivateKey)
        ghApp = GitHubBuilder().withJwtToken(jwtToken).build().app
    }

    override fun getInstallationAuthToken(installationId: Long): String {
        refreshJwtTokenIfStale()
        val appInstallation = ghApp!!.getInstallationById(installationId)
        return appInstallation.createToken().create().token
    }

}