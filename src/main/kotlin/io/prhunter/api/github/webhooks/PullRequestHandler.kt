package io.prhunter.api.github.webhooks

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.GithubAppService
import io.prhunter.api.github.webhooks.model.PullRequestWebhook
import io.prhunter.api.user.UserAccountRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class PullRequestHandler(
    private val bountyService: BountyService,
    private val userAccountRepository: UserAccountRepository,
    private val githubAppService: GithubAppService
) {

    fun handleMerged(details: PullRequestWebhook) {
        val bounties = getMatchingBountiesForPr(details)
        val userAccount = userAccountRepository.findByGithubUserId(details.sender.id)
        // TODO instead of logging, schedule a task that will comment on the PR prompting the user to fill in his details first so that he can claim the reward
        if (userAccount?.githubUserId == null || userAccount.ethWalletAddress == null) {
            log.info { "The PR was opened by a user without a wallet or github account linked. Ignoring" }
            return
        }
        bounties.forEach { bounty ->
            val fullName = "${bounty.repoOwner}/${bounty.repoName}"
            if (fullName == details.repository.fullName) {
                log.info { "Completing bounty ${bounty.id}" }
                bountyService.completeBounty(bounty, userAccount)
            } else {
                log.info { "Bounty repo $fullName does not match PR repo ${details.repository.fullName}" }
            }
        }
    }

    fun handleOpened(details: PullRequestWebhook) {
        val bounties = getMatchingBountiesForPr(details)
        val userAccount = userAccountRepository.findByGithubUserId(details.sender.id)
        // TODO instead of logging, schedule a task that will comment on the PR prompting the user to fill in his details first so that he can claim the reward
        if (userAccount?.githubUserId == null || userAccount.ethWalletAddress == null) {
            log.info { "The PR was opened by a user without a wallet or github account linked. Ignoring" }
            return
        }

        bounties.forEach { bounty ->
            // for each bounty
            // comment on the PR that it is linked to that bounty
            // if it actually is, that is
            val fullName = "${bounty.repoOwner}/${bounty.repoName}"
            if (fullName == details.repository.fullName) {
                log.info { "Observed a linked PR at ${details.repository.fullName}/#${details.number}" }
                githubAppService.newPullRequestComment(bounty, details.number, details.sender.htmlUrl)
            } else {
                log.info { "Bounty repo $fullName does not match PR repo ${details.repository.fullName}" }
            }
        }
    }

    private fun getMatchingBountiesForPr(details: PullRequestWebhook): List<Bounty> {
        val allActiveBounties = bountyService.getByStatus(BountyStatus.ACTIVE)
        val matchingBounties =
            allActiveBounties.filter { bounty -> details.pullRequest.body?.contains(bounty.id.toString()) ?: false }

        if (matchingBounties.isEmpty()) {
            log.info { "No matching bounties found" }
        } else {
            log.info { "Found ${matchingBounties.size} matching bounties for a new PR" }
        }
        return matchingBounties
    }
}