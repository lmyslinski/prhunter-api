package io.prhunter.api.github.auth

import io.prhunter.api.github.client.GithubRestClient
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
class GithubTokenController(
    val githubTokenRepository: GithubTokenRepository,
    val githubClient: GithubRestClient
) {

    @PostMapping("/github/token")
    fun updateUserGithubAccessToken(@RequestBody githubTokenRequest: GithubTokenRequest){
        log.info { "Received user GH token: $githubTokenRequest" }
        val ghUserData = runBlocking {
            githubClient.getGithubUserData(githubTokenRequest.accessToken)
        }
        val ghToken = GithubToken(githubTokenRequest.firebaseUserId, ghUserData.id, githubTokenRequest.accessToken)
        githubTokenRepository.save(ghToken)
        log.info { "User GH token updated" }
    }
}

data class GithubTokenRequest(
    var firebaseUserId: String,
    var accessToken: String
)