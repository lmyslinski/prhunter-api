package io.prhunter.api.github.webhooks

import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.webhooks.model.PullRequestWebhook
import io.prhunter.api.user.UserAccountRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class PullRequestHandler(
    private val bountyService: BountyService,
    private val userAccountRepository: UserAccountRepository,
) {

    fun handlePullRequestMerged(details: PullRequestWebhook) {
        val allActiveBounties = bountyService.getByStatus(BountyStatus.ACTIVE)

        val matchingBounties =
            allActiveBounties.filter { bounty -> details.pullRequest.body.contains(bounty.id.toString()) }

        if(matchingBounties.isEmpty()){
            log.info { "No matching bounties found" }
            return
        }else{
            log.info { "Found ${matchingBounties.size} matching bounties for a merged PR" }
        }

        val userAccount = userAccountRepository.findByGithubUserId(details.sender.id)

        if (userAccount?.githubUserId == null || userAccount.ethWalletAddress == null) {
            log.info { "The PR was merged by a user without a wallet or github account linked. Ignoring" }
            return
        }

        matchingBounties.forEach { bounty ->
            val fullName = "${bounty.repoOwner}/${bounty.repoName}"
            if (fullName == details.pullRequest.repoDetails.fullName) {
                log.info { "Completing bounty ${bounty.id}" }
                bountyService.completeBounty(bounty, userAccount)
            } else {
                log.info { "Bounty repo $fullName does not match PR repo ${details.pullRequest.repoDetails.fullName}" }
            }
        }
    }
}