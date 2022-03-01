package io.prhunter.api.github.serverauth

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.prhunter.api.github.GithubSecrets
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.kohsuke.github.GHApp
import org.kohsuke.github.GitHubBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.transaction.Transactional

private val log = KotlinLogging.logger {}

@Service
@Profile("!test")
class GithubAuthServiceImpl(
    private val githubSecrets: GithubSecrets,
    private val githubServerAuthRepository: GithubServerAuthRepository
) : GithubAuthService {

    override fun getInstallationAuthToken(installationId: Long): String {
        val appInstallation = getGithubAppClient().getInstallationById(installationId)
        return appInstallation.createToken().create().token
    }

    private fun getGithubAppClient(): GHApp {
        val appDataOpt = githubServerAuthRepository.findById(githubSecrets.appId)
        if (appDataOpt.isEmpty || appDataOpt.isPresent && isOlderThan5Minutes(appDataOpt.get().lastUpdateTime)) {
            generateNewGhAppToken()
        } else {
            return GitHubBuilder().withJwtToken(appDataOpt.get().jwtToken).build().app
        }
    }

    private fun isOlderThan5Minutes(updateTime: Instant): Boolean =
        Instant.now().minus(5, ChronoUnit.MINUTES).isAfter(updateTime)

    private fun generateNewGhAppToken(): GHApp {
        val tmpPrivateKey = GithubJwtService.generateTmpPrivateKey(githubSecrets.privateKey)
        val jwtToken = GithubJwtService.generateJwtKey(githubSecrets.appId, tmpPrivateKey)
        githubServerAuthRepository.save(GithubServerAuthInfo(githubSecrets.appId, jwtToken, Instant.now()))
        log.info { "Refreshed jwt token for app ${githubSecrets.appId}: $jwtToken" }
        return GitHubBuilder().withJwtToken(jwtToken).build().app
    }
}