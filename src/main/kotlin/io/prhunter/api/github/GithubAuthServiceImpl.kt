package io.prhunter.api.github

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import org.kohsuke.github.GHApp
import org.kohsuke.github.GitHubBuilder
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

private val log = KotlinLogging.logger {}

@Service
@Profile("!test")
class GithubAuthServiceImpl(
    private val githubSecrets: GithubSecrets
) : GithubAuthService {

    companion object{
        private const val APP_KEY = "PRHUNTER"
    }

    @PostConstruct
    private fun initJwtCache(){
        cache.put(APP_KEY, getApplicationGithubClient())
    }

    // We need to regenerate the application JWT every 5 minutes since max JWT expiration is 10 minutes
    private val cache: Cache<String, GHApp> = Caffeine.newBuilder()
        .refreshAfterWrite(5, TimeUnit.MINUTES)
        .build { getApplicationGithubClient() }

    private fun getApplicationGithubClient(): GHApp {
        log.info { "Refreshing application JWT key" }
        val tmpPrivateKey = GithubJwtService.generateTmpPrivateKey(githubSecrets.privateKey)
        val jwtToken = GithubJwtService.generateJwtKey(githubSecrets.appId, tmpPrivateKey)
        return GitHubBuilder().withJwtToken(jwtToken).build().app
    }

    override fun getInstallationAuthToken(installationId: Long): String {
        val appInstallation = cache.getIfPresent(APP_KEY)!!.getInstallationById(installationId)
        return appInstallation.createToken().create().token
    }

}