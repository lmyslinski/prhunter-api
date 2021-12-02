package io.prhunter.api.github

import mu.KotlinLogging
import org.kohsuke.github.GHApp
import org.kohsuke.github.GitHubBuilder
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
@Profile("!test")
class GithubAuthServiceImpl(
    private val githubSecrets: GithubSecrets
) : GithubAuthService {

    private var ghApp: GHApp? = null

    companion object {
        // every 5 minutes
        const val REFRESH_INTERNAL: Long = (1000 * 60 * 5).toLong()
    }

    @Scheduled(fixedRate = REFRESH_INTERNAL)
    private fun getApplicationGithubClient() {
        log.info { "Refreshing application JWT key" }
        val tmpPrivateKey = GithubJwtService.generateTmpPrivateKey(githubSecrets.privateKey)
        val jwtToken = GithubJwtService.generateJwtKey(githubSecrets.appId, tmpPrivateKey)
        ghApp = GitHubBuilder().withJwtToken(jwtToken).build().app
    }

    override fun getInstallationAuthToken(installationId: Long): String {
        val appInstallation = ghApp!!.getInstallationById(installationId)
        return appInstallation.createToken().create().token
    }

}