package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.installation.Installation
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.github.webhooks.model.WebhookBody
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
    private val installationService: InstallationService
) {

    @PostMapping()
    fun handleWebhook(@RequestBody eventBody: String): ResponseEntity<String> {
        log.debug { "Webhook received: $eventBody" }
        val eventTree = objectMapper.readTree(eventBody)
        when (eventTree.get("action").asText()) {
            "created" -> {
                val webhookDetails = objectMapper.readValue<WebhookBody>(eventBody)
                installationService.registerInstallation(
                    Installation(
                        webhookDetails.installation.id,
                        webhookDetails.installation.account.id,
                        webhookDetails.installation.account.type,
                        webhookDetails.sender.id,
                        webhookDetails.sender.type
                    )
                )
            }
            "deleted" -> {
                val webhookDetails = objectMapper.readValue<WebhookBody>(eventBody)
                val installationId = webhookDetails.installation.id
                installationService.removeInstallation(installationId)
            }
            else -> {
                log.info { "Webhook ignored" }
            }
        }
        return ResponseEntity.ok().body("Webhook handled successfully")
    }
}


