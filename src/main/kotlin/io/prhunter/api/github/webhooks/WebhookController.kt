package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.github.GithubSecrets
import io.prhunter.api.github.webhooks.model.InstallationWebhook
import io.prhunter.api.github.webhooks.model.IssueWebhook
import io.prhunter.api.github.webhooks.model.PullRequestWebhook
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/webhook")
class WebhookController(
    private val objectMapper: ObjectMapper,
    private val githubSecrets: GithubSecrets,
    private val pullRequestHandler: PullRequestHandler,
    private val installationHandler: InstallationHandler,
    private val issueHandler: IssueHandler
) {

    @PostMapping()
    fun receiveWebhook(@RequestBody eventBody: String): ResponseEntity<String> {
        log.debug { "Webhook received: $eventBody" }
        validateWebhook()
        handleWebhook(eventBody)
        return ResponseEntity.ok().body("")
    }

    private fun handleWebhook(body: String) {
        val eventTree = objectMapper.readTree(body)
        var handled = false

        if (eventTree.get("pull_request") != null) {
            val pull_request = eventTree.get("pull_request")
            val action = eventTree.get("action")
            val issueUrl = pull_request.get("issue_url")
            val merged = pull_request.get("merged")
            if (action.textValue() == "closed" && issueUrl != null && merged.asBoolean()) {
                val webhook = objectMapper.readValue<PullRequestWebhook>(body)
                // what if a PR closes multiple issues?
                pullRequestHandler.handlePullRequestMerged(webhook)
                handled = true
            }
        }

        // make sure that only the minimal set of data is present on the installation request
        if (eventTree.count() == 4) {
            val action = eventTree.get("action")
            val repos = eventTree.get("repositories")
            val installation = eventTree.get("installation")
            val sender = eventTree.get("sender")

            if (action != null && repos != null && installation != null && sender != null) {
                val webhook = objectMapper.readValue<InstallationWebhook>(body)
                installationHandler.handle(webhook)
                handled = true
            }
        }

        if(eventTree.get("issue") != null){
            val action = eventTree.get("action")
            if(action.textValue() == "closed"){
                val webhook = objectMapper.readValue<IssueWebhook>(body)
                issueHandler.handleIssueClosed(webhook)
                handled = true
            }
        }

        if(!handled) {
            log.info { "A webhook event was ignored: $body" }
        }
    }

    private fun validateWebhook() {
        // TODO use webhook secret to check that the webhook comes from Github
    }
}


