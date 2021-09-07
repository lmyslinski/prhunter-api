package io.prhunter.api.github.client

data class GHRepoData(
    val id: Long,
    val name: String,
    val fullName: String,
    val private: Boolean,
)