package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.GithubAppInstallationService
import io.prhunter.api.github.auth.GithubTokenRepository
import io.prhunter.api.github.webhooks.model.PullRequestWebhook
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class PullRequestHandler(
    private val objectMapper: ObjectMapper,
    private val bountyService: BountyService,
    private val githubTokenRepository: GithubTokenRepository,
    private val githubAppInstallationService: GithubAppInstallationService
) {

    fun handle(body: String) {
        val webhookDetails = objectMapper.readValue<PullRequestWebhook>(body)
        when (webhookDetails.action) {
            "closed" -> {
                if(webhookDetails.pullRequest.issueUrl != null && webhookDetails.pullRequest.merged){
                    fetchAndVerifyData(webhookDetails)
                }else{
                    log.info { "A PR was closed but wasn't liked to an issue or wasn't merged" }
                }
            }
            "opened" -> {
                log.info { "A new PR was opened" }
            }
            else -> {
                log.info { "Pull request webhook ignored" }
            }
        }
    }

    fun fetchAndVerifyData(details: PullRequestWebhook){
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
        val githubToken = githubTokenRepository.findByGithubUserId(details.sender.id)

        if(githubToken == null){
            log.info { "The PR was merged by a user without a PRHunter account linked with Github. Ignoring" }
            return
        }

        bountyService.completeBounty(bounty, githubToken.firebaseUserId)
        log.info { "Pull request was completed successfully" }
    }

}