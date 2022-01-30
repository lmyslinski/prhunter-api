package io.prhunter.api.github.webhooks

import io.prhunter.api.bounty.BountyService
import io.prhunter.api.github.webhooks.model.IssueWebhook
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class IssueHandler(
    private val bountyService: BountyService,
) {
    fun handleIssueClosed(issueWebhook: IssueWebhook){
        log.info { "An issue was closed"  }
        GlobalScope.launch {  }
        // get bounty linked with this issue
        // cancel it if no PR was merged

    }

    // TODO use a db-scheduler for this
    suspend fun closeTheIssueIfBountyNotCompleted(){

    }
}