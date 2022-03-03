package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.github.GithubSecrets
import io.prhunter.api.github.webhooks.model.InstallationWebhook
import io.prhunter.api.github.webhooks.model.IssueWebhook
import io.prhunter.api.github.webhooks.model.PullRequestWebhook
import kotlinx.coroutines.*
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
        validateWebhook()
        handleWebhook(eventBody)
        return ResponseEntity.ok().body("")
    }

    private fun handleWebhook(body: String) {
        val eventTree = objectMapper.readTree(body)
        log.debug { "Webhook received: $eventTree" }
        var handled = false

        if (eventTree.get("pull_request") != null) {
            val pull_request = eventTree.get("pull_request")
            val action = eventTree.get("action")
            val merged = pull_request.get("merged")
            if (action.textValue() == "closed" && merged.asBoolean()) {
                val webhook = objectMapper.readValue<PullRequestWebhook>(body)
                runBlocking {
                    launch {
                        pullRequestHandler.handleMerged(webhook)
                    }
                }
                handled = true
            }else if (action.textValue() == "opened" || action.textValue() == "reopened"){
                val webhook = objectMapper.readValue<PullRequestWebhook>(body)
                runBlocking {
                    launch {
                        pullRequestHandler.handleOpened(webhook)
                    }
                }
                handled = true
            }
        }

        // make sure that only the minimal set of data is present on the installation request
        if (isInstallationEvent(eventTree)) {
            val action = eventTree.get("action")
            val repos = eventTree.get("repositories")
            val installation = eventTree.get("installation")
            val sender = eventTree.get("sender")

            if (action != null && repos != null && installation != null && sender != null) {
                val webhook = objectMapper.readValue<InstallationWebhook>(body)
                runBlocking {
                    launch {
                        installationHandler.handle(webhook)
                    }
                }
                handled = true
            }
        }

        if(eventTree.get("issue") != null){
            val action = eventTree.get("action")
            if(action.textValue() == "closed"){
                val webhook = objectMapper.readValue<IssueWebhook>(body)
                runBlocking {
                    launch {
                        issueHandler.handleIssueClosed(webhook.issue.id)
                    }
                }
                handled = true
            }
        }

        if(!handled) {
            log.info { "A webhook event was ignored: $eventTree" }
        }
    }

    private fun validateWebhook() {
        // TODO use webhook secret to check that the webhook comes from Github
    }

    private fun isInstallationEvent(eventTree: JsonNode): Boolean {
        val stupidGithubApiWorkaround = (eventTree.count() == 5) && eventTree.get("requester").isNull
        return eventTree.count() == 4 || stupidGithubApiWorkaround
    }
}


