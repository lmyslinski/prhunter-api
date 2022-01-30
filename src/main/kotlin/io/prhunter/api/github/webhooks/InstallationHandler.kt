package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import io.prhunter.api.github.webhooks.model.InstallationWebhook
import io.prhunter.api.installation.Installation
import io.prhunter.api.installation.InstallationService
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class InstallationHandler(
    private val installationService: InstallationService
) {

    fun handle(webhook: InstallationWebhook) {
        when (webhook.action) {
            "created" -> installationService.registerInstallation(
                Installation(
                    webhook.installation.id,
                    webhook.installation.account.id,
                    webhook.installation.account.type,
                    webhook.sender.id,
                    webhook.sender.type
                )
            )
            "deleted" -> {
                installationService.removeInstallation(webhook.installation.id)
            }
            else -> {
                log.info { "Installation webhook ignored" }
            }
        }
    }
}