package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.GithubAppInstallationService
import io.prhunter.api.user.UserAccountRepository
import io.prhunter.api.github.webhooks.model.PullRequestWebhook
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class PullRequestHandler(
    private val bountyService: BountyService,
    private val userAccountRepository: UserAccountRepository,
    private val githubAppInstallationService: GithubAppInstallationService
) {

    fun handlePullRequestMerged(details: PullRequestWebhook) {
        val issue = runBlocking {
            githubAppInstallationService.fetchIssue(details.pullRequest.issueUrl!!, details.installation.id)
        }
        val bounty = bountyService.getBountyByIssueId(issue.id)

        // verify that bounty exists for this issue
        if(bounty == null){
            log.info { "No bounty found for issue ${issue.title}. Ignoring" }
            return
        }

        // verify that the bounty is in active state
        if(bounty.bountyStatus != BountyStatus.ACTIVE){
            log.info { "Bounty is in invalid state. Ignoring" }
            return
        }

        // TODO verify that the PR sender has a PRHunter account with Github and a wallet linked
        val githubToken = userAccountRepository.findByGithubUserId(details.sender.id)

        if(githubToken == null){
            log.info { "The PR was merged by a user without a PRHunter account linked with Github. Ignoring" }
            return
        }

        bountyService.completeBounty(bounty, githubToken.firebaseUserId)
    }
}