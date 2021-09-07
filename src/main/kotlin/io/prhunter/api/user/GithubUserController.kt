package io.prhunter.api.user

import io.prhunter.api.github.GithubService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Issue
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
class GithubUserController(
    val githubService: GithubService,
) {

    @GetMapping("/repo")
    fun listRepositories(principal: Principal): List<GHRepoData> {
        return githubService.listRepositories(getCurrentUser(principal))
    }

    @GetMapping("/{owner}/{repo}/issues")
    fun listIssues(@PathVariable owner: String, @PathVariable repo: String, principal: Principal): List<Issue> {
        return githubService.listIssues(owner, repo, getCurrentUser(principal).accessToken)
    }

    private fun getCurrentUser(principal: Principal): GithubUser =
        ((principal as OAuth2AuthenticationToken).principal as GithubUser)

}