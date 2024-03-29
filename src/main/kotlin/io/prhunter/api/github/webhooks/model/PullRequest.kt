package io.prhunter.api.github.webhooks.model

data class PullRequestWebhook(
    val sender: AccountDetails,
    val action: String,
    val number: Long,
    val pullRequest: PullRequestDetails,
    val installation: InstallationId,
    val repository: RepoDetails
)

data class PullRequestDetails(
    val merged: Boolean,
    val body: String?,
    val user: UserDetails
)

data class UserDetails(
    val id: Long
)

data class RepoDetails(
    val fullName: String
)

data class InstallationId(
    val id: Long
)
