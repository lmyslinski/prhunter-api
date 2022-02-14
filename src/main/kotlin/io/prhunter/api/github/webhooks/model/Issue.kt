package io.prhunter.api.github.webhooks.model

data class IssueWebhook(
    val installation: InstallationId,
    val sender: AccountDetails,
    val action: String,
    val issue: IssueDetails
)

data class IssueDetails(
    val pullRequest: IssuePullRequestDetails?,
    val id: Long,
    val number: Long
)

data class IssuePullRequestDetails(
    val url: String
)

