package io.prhunter.api.search

data class PageResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val total: Int
)
