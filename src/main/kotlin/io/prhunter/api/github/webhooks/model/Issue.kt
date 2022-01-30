package io.prhunter.api.github.webhooks.model

data class IssueWebhook(
    val installation: InstallationId,
    val sender: AccountDetails,
    val action: String,
    val issue: IssueDetails
)

data class IssueDetails(
    val pullRequest: IssuePullRequestDetails?
)

data class IssuePullRequestDetails(
    val url: String
)

