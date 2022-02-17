package io.prhunter.api.repository

import io.prhunter.api.RequestUtil
import io.prhunter.api.github.GithubUserService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.Issue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/repo")
class RepositoryController(
    val githubUserService: GithubUserService,
) {

    @GetMapping()
    fun listRepositories(principal: Principal): List<GHRepoData> {
        val currentUser = RequestUtil.getUserFromRequest(principal)
        return githubUserService.listUserInstallationRepositories(currentUser)
    }

    @GetMapping("/{owner}/{repo}/issues")
    fun listIssues(@PathVariable owner: String, @PathVariable repo: String, principal: Principal): List<Issue> {
        val currentUser = RequestUtil.getUserFromRequest(principal)
        return githubUserService.listRepositoryIssues(owner, repo, currentUser)
    }

}