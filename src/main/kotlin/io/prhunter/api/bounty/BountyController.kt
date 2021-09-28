package io.prhunter.api.bounty

import io.prhunter.api.RequestUtil
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/bounty")
class BountyController(private val bountyService: BountyService) {

    @GetMapping
    fun listBounties(): List<Bounty> {
        return bountyService.list()
    }

    @PostMapping
    fun createBounty(
        @RequestBody createBountyRequest: CreateBountyRequest,
        principal: Principal
    ): ResponseEntity<Bounty> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            bountyService.createBounty(
                createBountyRequest,
                RequestUtil.getUserFromRequest(principal).accessToken
            )
        )
    }

    @GetMapping("/{id}")
    fun getBounty(@PathVariable id: Long): Bounty? {
        return bountyService.getBounty(id)
    }

    @PutMapping("/{id}")
    fun updateBounty(
        @PathVariable id: Long,
        @RequestBody updateBountyRequest: UpdateBountyRequest
    ): ResponseEntity<Bounty> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(bountyService.updateBounty(id, updateBountyRequest))
    }
}