package io.prhunter.api.search

import io.prhunter.api.bounty.api.BountyView
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class SearchController(
    private val searchService: SearchService
) {

    @PostMapping("/bounty/search")
    fun search(
        @RequestBody searchRequest: SearchRequest,
        request: HttpServletRequest
    ): PageResponse<BountyView> {
        return searchService.search(request, searchRequest)
    }
}