package io.prhunter.api.github.auth

import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.user.UserAccountService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
class GithubTokenController(
    val userAccountService: UserAccountService,
    val githubClient: GithubRestClient
) {

    @PostMapping("/github/token")
    fun updateUserGithubAccessToken(@RequestBody githubTokenRequest: GithubTokenRequest){
        log.debug { "Received user GH token: $githubTokenRequest" }
        userAccountService.updateGithubToken(githubTokenRequest)
        log.debug { "User GH token updated" }
    }
}

data class GithubTokenRequest(
    var firebaseUserId: String,
    var accessToken: String
)