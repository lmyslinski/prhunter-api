package io.prhunter.api.webhooks.model

data class InstallationCreated(
    val installation: InstallationDetails,
    val sender: AccountDetails
)

data class InstallationDetails(
    val id: Long,
    val account: AccountDetails,
)

data class AccountDetails(
    val id: Long,
    val type: String
)