package io.prhunter.api.user

import mu.KotlinLogging
import org.kohsuke.github.GitHubBuilder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/user")
class GithubUserController {

    private val log = KotlinLogging.logger {}

    @GetMapping("/repo")
    fun listRepositories(principal: Principal): String {
        val repos = GitHubBuilder().withOAuthToken(getUserAccessToken(principal)).build().listAllPublicRepositories().toList()
        log.info { repos }
        return repos.toString()
    }

    //    @GetMapping("/repo/${id}/issues")
    fun listIssues(@PathVariable repoId: Long) {

    }

    private fun getUserAccessToken(principal: Principal): String =
        ((principal as OAuth2AuthenticationToken).principal as GithubUser).accessToken


}