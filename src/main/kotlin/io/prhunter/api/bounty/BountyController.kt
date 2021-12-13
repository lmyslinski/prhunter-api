package io.prhunter.api.bounty

import io.prhunter.api.RequestUtil
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import io.prhunter.api.crypto.CoinGeckoApiService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/bounty")
class BountyController(
    private val bountyService: BountyService,
    private val coinGeckoApiService: CoinGeckoApiService
) {

    @GetMapping
    fun listBounties(): List<BountyView> {
        return bountyService.list().map { it.toView(coinGeckoApiService.getCurrentEthUsdPrice()) }
    }

    @PostMapping
    fun createBounty(
        @RequestBody createBountyRequest: CreateBountyRequest,
        principal: Principal
    ): ResponseEntity<BountyView> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        val bounty = bountyService.createBounty(
            createBountyRequest,
            firebaseUser
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(bounty.toView(coinGeckoApiService.getCurrentEthUsdPrice()))
    }

    @GetMapping("/{id}")
    fun getBounty(@PathVariable id: Long): BountyView? {
        return bountyService.getBounty(id).toView(coinGeckoApiService.getCurrentEthUsdPrice())
    }

    @PutMapping("/{id}")
    fun updateBounty(
        @PathVariable id: Long,
        @RequestBody updateBountyRequest: UpdateBountyRequest,
        principal: Principal
    ): ResponseEntity<BountyView> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(bountyService.updateBounty(id, updateBountyRequest, firebaseUser).toView(coinGeckoApiService.getCurrentEthUsdPrice()))
    }
}