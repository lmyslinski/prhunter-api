package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.github.webhooks.model.InstallationWebhook
import io.prhunter.api.installation.Installation
import io.prhunter.api.installation.InstallationService
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class InstallationHandler(
    val objectMapper: ObjectMapper,
    val installationService: InstallationService
) {

    fun handle(body: String) {
        val webhookDetails = objectMapper.readValue<InstallationWebhook>(body)
        when(webhookDetails.action){
            "created" -> installationService.registerInstallation(
                Installation(
                    webhookDetails.installation.id,
                    webhookDetails.installation.account.id,
                    webhookDetails.installation.account.type,
                    webhookDetails.sender.id,
                    webhookDetails.sender.type
                )
            )
            "deleted" -> {
                installationService.removeInstallation(webhookDetails.installation.id)
            }
            else ->{
                log.info { "Installation webhook ignored" }
            }
        }
    }
}