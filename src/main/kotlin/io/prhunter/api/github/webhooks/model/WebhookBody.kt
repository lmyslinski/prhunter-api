package io.prhunter.api.github.webhooks.model

data class WebhookBody(
    val installation: InstallationDetails,
    val sender: AccountDetails,
    val action: String
)

data class InstallationDetails(
    val id: Long,
    val account: AccountDetails,
)

data class AccountDetails(
    val id: Long,
    val type: String
)