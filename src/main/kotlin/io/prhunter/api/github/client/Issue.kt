package io.prhunter.api.github.client

data class Issue(
    val id: Long,
    val nodeId: String,
    val title: String,
    val state: String,
    val body: String?,
    val pullRequest: PullRequest?,
    val number: Long,
)

data class PullRequest(
    val url: String
)