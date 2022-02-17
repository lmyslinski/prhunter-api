package io.prhunter.api.github.webhooks.model

data class PullRequestWebhook(
    val sender: AccountDetails,
    val action: String,
    val pullRequest: PullRequestDetails,
    val installation: InstallationId,
)

data class PullRequestDetails(
    val merged: Boolean,
    val issueUrl: String?,
    val body: String,
    val repoDetails: RepoDetails
)

data class RepoDetails(
    val fullName: String
)

data class InstallationId(
    val id: Long
)
