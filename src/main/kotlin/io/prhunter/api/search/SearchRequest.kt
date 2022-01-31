package io.prhunter.api.search

import io.prhunter.api.bounty.BountyType
import io.prhunter.api.bounty.Experience
import io.prhunter.api.crypto.CryptoCurrency
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal

data class SearchRequest(
        val pageable: PageableImpl? = null,
        val sortable: SortableImpl? = null,
        val experience: Experience? = null,
        val language: String? = null,
        val price: PriceFilterParams? = null,
        val tags: List<String>? = null,
        val bountyType: BountyType? = null,
        val contentContains: String? = null,
        val currency: CryptoCurrency? = null
)

data class LocationFilterParams(
        val id: Long,
        val type: String
)

data class PriceFilterParams(
        @Schema(description="Optional, defaults to 0", example = "11.1")
        val min: BigDecimal? = BigDecimal.valueOf(0L),
        @Schema(description = "Optional, defaults to a very big number", example = "34.4")
        val to: BigDecimal? = BigDecimal.valueOf(Long.MAX_VALUE),

)

data class SortableImpl(
        @Schema(description = "Not yet supported", example = "updatedAt")
        val property: String,
        @Schema(description = "Defaults to 10", example = "desc")
        val direction: String
)

data class PageableImpl(
        @Schema(description = "Pagination starts at 1", example = "1")
        val page: Int,
        @Schema(description = "Defaults to 10", example = "10")
        val size: Int
)

fun PageableImpl.toPageRequest(sort: Sort): PageRequest = PageRequest.of(page, size, sort)

fun SortableImpl.toSort(): Sort = Sort.by(Sort.Direction.valueOf(direction.uppercase()), property)