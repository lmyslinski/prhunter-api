package io.prhunter.api.github.client

data class GHRepoPermissionData(
    val id: Long,
    val name: String,
    val fullName: String,
    val private: Boolean,
    val permissions: Permissions
)

data class Permissions(
    val admin: Boolean,
    val push: Boolean,
    val pull: Boolean
)