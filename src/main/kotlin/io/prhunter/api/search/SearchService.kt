package io.prhunter.api.search

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.generated.tables.Bounty.BOUNTY
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.SortField
import org.jooq.impl.DSL
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SearchService(
    private val dslContext: DSLContext,
    private val bountyService: BountyService
) {

    fun search(request: HttpServletRequest, searchRequest: SearchRequest): PageResponse<BountyView> {
        val pageable = getPageable(searchRequest)
        val conditions = listOfNotNull(
            getExperienceFilter(searchRequest),
            getLanguageFilter(searchRequest),
            getPriceFilter(searchRequest),
            getCurrencyFilter(searchRequest),
            getBountyTypeFilter(searchRequest),
            getTitleOrBodyFilter(searchRequest),
            getTagsFilter(searchRequest),

        )
        val selectQuery = dslContext.selectFrom(BOUNTY).where(getWhereCond(conditions))
            .orderBy(getOrderBy(searchRequest))
            .offset((pageable.pageNumber * pageable.pageSize).toLong())
            .limit(pageable.pageSize)

        val total = selectQuery.fetch().size
        val results = selectQuery.fetchInto(Bounty::class.java)
        val bountyViews = results.map { bountyService.toView(it) }
        return PageResponse(bountyViews, pageable.pageNumber + 1, total)
    }

    private fun getBountyTypeFilter(searchRequest: SearchRequest): Condition? {
        return if (searchRequest.bountyType != null) {
            BOUNTY.BOUNTY_TYPE.eq(searchRequest.bountyType.name)
        } else null
    }

    private fun getTitleOrBodyFilter(searchRequest: SearchRequest): Condition? {
        return if (!searchRequest.contentContains.isNullOrEmpty()) {
            BOUNTY.TITLE.contains(searchRequest.contentContains)
                .or(BOUNTY.PROBLEM_STATEMENT.contains(searchRequest.contentContains))
                .or(BOUNTY.ACCEPTANCE_CRITERIA.contains(searchRequest.contentContains))
        } else null
    }

    private fun getTagsFilter(searchRequest: SearchRequest): Condition? {
        return if (!searchRequest.tags.isNullOrEmpty()){
          BOUNTY.TAGS.contains(searchRequest.tags.toTypedArray())
        } else null
    }

    private fun getPageable(searchRequest: SearchRequest): Pageable {
        val sortable = searchRequest.sortable?.toSort() ?: Sort.by(Sort.Direction.DESC, "updatedAt")
        return searchRequest.pageable?.toPageRequest(sortable) ?: PageRequest.of(0, 12, sortable)
    }

    private fun getExperienceFilter(searchRequest: SearchRequest): Condition? {
        return if (searchRequest.experience != null) {
            BOUNTY.EXPERIENCE.eq(searchRequest.experience.name)
        } else null
    }

    private fun getCurrencyFilter(searchRequest: SearchRequest): Condition? {
        return if (searchRequest.currency != null) {
            BOUNTY.BOUNTY_CURRENCY.eq(searchRequest.currency.name)
        } else null
    }

    private fun getPriceFilter(searchRequest: SearchRequest): Condition? {
        return if (searchRequest.price != null) {
                BOUNTY.BOUNTY_VALUE_USD.between(searchRequest.price.min, searchRequest.price.to)
        } else null
    }

    private fun getLanguageFilter(searchRequest: SearchRequest): Condition? {
        return if (searchRequest.language != null && searchRequest.language.isNotEmpty()) {
            BOUNTY.LANGUAGES.`in`(arrayOf(searchRequest.language))
        } else null
    }

    private fun getWhereCond(conditions: List<Condition?>): Condition {
        return if (conditions.isEmpty()) {
            DSL.trueCondition()
        } else {
            DSL.and(conditions)
        }
    }

    private fun getOrderBy(searchRequest: SearchRequest): SortField<out Any> {
        val searchProperty = when (searchRequest.sortable?.property) {
            else -> BOUNTY.CREATED_AT
        }
        return if (searchRequest.sortable?.direction == "asc")
            searchProperty.asc()
        else
            searchProperty.desc()
    }
}