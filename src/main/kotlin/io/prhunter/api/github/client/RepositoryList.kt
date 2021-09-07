package io.prhunter.api.github.client

data class RepositoryList(val totalCount: Long,
                          val repositories: List<GHRepoData>
)