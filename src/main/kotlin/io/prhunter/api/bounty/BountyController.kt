package io.prhunter.api.bounty

import io.prhunter.api.RequestUtil
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.CreateBountyResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*

@RestController
@RequestMapping("/bounty")
class BountyController(
    private val bountyService: BountyService,
    private val featuredBountyService: FeaturedBountyService
) {

    @GetMapping
    fun listBounties(): List<BountyView> {
        return bountyService.list()
    }

    @GetMapping("/featured")
    fun getFeaturedBounties(): List<BountyView> {
        return featuredBountyService.getFeaturedBounties()
    }

    @PostMapping
    fun createBounty(
        @RequestBody createBountyRequest: CreateBountyRequest,
        principal: Principal
    ): ResponseEntity<CreateBountyResponse> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        val bountyView = bountyService.getOrCreateBounty(
            createBountyRequest,
            firebaseUser
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(bountyView)
    }

    @GetMapping("/{id}")
    fun getBounty(@PathVariable id: UUID): BountyView? {
        return bountyService.getBountyView(id)
    }

    @GetMapping("/issue/{issueId}")
    fun bountyExists(@PathVariable issueId: Long): ResponseEntity<Any> {
        val bountyView = bountyService.getBountyViewByIssueId(issueId)
        return if (bountyView == null) {
            ResponseEntity.status(200).body("")
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body(bountyView)
        }
    }
}