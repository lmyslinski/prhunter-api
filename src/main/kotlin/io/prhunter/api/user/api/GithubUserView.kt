package io.prhunter.api.user.api

data class GithubUserView (
    var id: Long,
    var login: String,
    var email: String?,
    var fullName: String?,
)