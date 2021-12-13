package io.prhunter.api.user

import io.prhunter.api.RequestUtil
import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.api.BountyView
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class UserController(
    val bountyService: BountyService
) {

    @GetMapping("/user")
    fun getUser(principal: Principal): ResponseEntity<FirebaseUser> {
        val user = RequestUtil.getUserFromRequest(principal)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/user/bounties")
    fun getUserBounties(principal: Principal): ResponseEntity<List<BountyView>> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        val userBounties = bountyService.getUserBounties(firebaseUser.id)
        return ResponseEntity.ok(userBounties);
    }
}