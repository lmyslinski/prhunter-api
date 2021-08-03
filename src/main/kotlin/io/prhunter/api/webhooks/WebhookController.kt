package io.prhunter.api.webhooks

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/webhook")
class WebhookController {

    @PostMapping()
    fun handleWebhook(@RequestBody eventBody: String): ResponseEntity<String> {
        log.info { "Webhook received: $eventBody" }
        return ResponseEntity.ok().body(eventBody)
    }

}


