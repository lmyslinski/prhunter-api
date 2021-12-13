package io.prhunter.api.github.auth

import mu.KotlinLogging
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@RestController
class GithubTokenController(
    val githubTokenRepository: GithubTokenRepository
) {

    @PostMapping("/github/token")
    fun updateUserGithubAccessToken(@RequestBody githubToken: GithubToken){
        log.info { "Received user GH token: $githubToken" }
        githubTokenRepository.save(githubToken)
        log.info { "User GH token updated" }
    }
}