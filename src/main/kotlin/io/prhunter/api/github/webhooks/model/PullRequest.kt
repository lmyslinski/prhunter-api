package io.prhunter.api.github.webhooks.model

data class PullRequestWebhook(
    val sender: AccountDetails,
    val action: String,
    val pullRequest: PullRequestDetails,
    val installation: InstallationDetails
)

data class PullRequestDetails(
    val merged: Boolean,
    val issueUrl: String?
)
