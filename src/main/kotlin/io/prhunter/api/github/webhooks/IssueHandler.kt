package io.prhunter.api.github.webhooks

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.BountyStatus
import io.prhunter.api.github.webhooks.model.IssueWebhook
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Instant

private val log = KotlinLogging.logger {}

data class CloseIssueData(val issueId: Long)

@Service
class IssueHandler(
    private val bountyService: BountyService,
    private val scheduler: Scheduler
) {

    private val issueCloseTask = Tasks.oneTime("issue-close-task", CloseIssueData::class.java).execute{ data, _ ->
        closeTheIssueIfBountyNotCompleted(data.data.issueId)
    }

    fun handleIssueClosed(issueWebhook: IssueWebhook){
        log.debug { "An issue was closed"  }
        val bounty = bountyService.getBountyByIssueId(issueWebhook.issue.id)
        if(bounty != null){
            log.debug { "Scheduling bounty closure for issue ${issueWebhook.issue.id}"  }
            scheduleBountyClosure(issueWebhook.issue.id)
        }
    }

    // When a PR is merged successfully, we first get an issue close event followed by the PR merge event
    // In order to prevent the accidental cancellation of a done bounty, we delay the closure check by 60 seconds
    // to make sure that the PR webhook is handled first
    fun scheduleBountyClosure(issueId: Long){
        // Schedule the task for execution a certain time in the future and optionally provide custom data for the execution
        scheduler.schedule(issueCloseTask.instance(issueId.toString(), CloseIssueData(issueId)), Instant.now().plusSeconds(30))
    }

    fun closeTheIssueIfBountyNotCompleted(issueId: Long) {
        val bounty = bountyService.getBountyByIssueId(issueId)
        if(bounty != null && (bounty.bountyStatus == BountyStatus.PENDING || bounty.bountyStatus == BountyStatus.ACTIVE)){
            bountyService.cancelBounty(bounty, "issue was closed")
        }
    }
}