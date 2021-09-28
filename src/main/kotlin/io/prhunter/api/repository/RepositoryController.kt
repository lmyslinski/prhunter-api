package io.prhunter.api.repository

import io.prhunter.api.RequestUtil
import io.prhunter.api.github.GithubService
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
    val githubService: GithubService,
) {

    @GetMapping()
    fun listRepositories(principal: Principal): List<GHRepoData> {
        return githubService.listRepositories(RequestUtil.getUserFromRequest(principal))
    }

    @GetMapping("/{owner}/{repo}/issues")
    fun listIssues(@PathVariable owner: String, @PathVariable repo: String, principal: Principal): List<Issue> {
        return githubService.listRepositoryIssues(owner, repo, RequestUtil.getUserFromRequest(principal).accessToken)
    }

}