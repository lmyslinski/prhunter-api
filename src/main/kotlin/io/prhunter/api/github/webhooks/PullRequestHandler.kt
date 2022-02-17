package io.prhunter.api.github.webhooks

import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.GithubAppService
import io.prhunter.api.github.GithubUserService
import io.prhunter.api.github.webhooks.model.PullRequestWebhook
import io.prhunter.api.user.UserAccountRepository
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class PullRequestHandler(
    private val bountyService: BountyService,
    private val userAccountRepository: UserAccountRepository,
    private val githubAppService: GithubAppService
) {

    fun handlePullRequestMerged(details: PullRequestWebhook) {
        val issue = runBlocking {
            githubAppService.fetchIssue(details.pullRequest.issueUrl!!, details.installation.id)
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
        val userAccount = userAccountRepository.findByGithubUserId(details.sender.id)


        if(userAccount?.githubUserId == null || userAccount.ethWalletAddress == null){
            log.info { "The PR was merged by a user without a wallet or github account linked. Ignoring" }
            return
        }

        bountyService.completeBounty(bounty, userAccount)
    }
}