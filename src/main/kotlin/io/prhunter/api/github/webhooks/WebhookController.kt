package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import io.prhunter.api.github.GithubSecrets
import io.prhunter.api.installation.InstallationService
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
    private val installationService: InstallationService,
    private val githubSecrets: GithubSecrets,
    private val pullRequestHandler: PullRequestHandler,
    private val installationHandler: InstallationHandler,
) {

    @PostMapping()
    fun receiveWebhook(@RequestBody eventBody: String): ResponseEntity<String> {
        log.debug { "Webhook received: $eventBody" }
        validateWebhook()
        handleWebhook(eventBody)
        return ResponseEntity.ok().body("")
    }

    private fun handleWebhook(body: String){
        val eventTree = objectMapper.readTree(body)
        if(eventTree.get("installation") != null){
            installationHandler.handle(body)
        }else if (eventTree.get("pull_request") != null){
            pullRequestHandler.handle(body)
        }
    }

    private fun validateWebhook(){
        // TODO use webhook secret to check that the webhook comes from Github
    }
}


