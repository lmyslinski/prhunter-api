package io.prhunter.api.search

import io.prhunter.api.bounty.Experience
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class SearchRequest(
        val pageable: PageableImpl? = null,
        val sortable: SortableImpl? = null,
        val experience: Experience? = null,
        val language: String? = null,
)

data class LocationFilterParams(
        val id: Long,
        val type: String
)

data class SortableImpl(
        val property: String,
        val direction: String
)

data class PageableImpl(
        val page: Int,
        val size: Int
)

fun PageableImpl.toPageRequest(sort: Sort): PageRequest = PageRequest.of(page, size, sort)

fun SortableImpl.toSort(): Sort = Sort.by(Sort.Direction.valueOf(direction.uppercase()), property)