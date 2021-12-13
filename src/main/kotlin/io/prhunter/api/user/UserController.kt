package io.prhunter.api.user

import io.prhunter.api.RequestUtil
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
    fun getUser(principal: Principal): ResponseEntity<String> {
        throw NotImplementedError()
//        val user = RequestUtil.getUserFromRequest(principal).toView()
//        return ResponseEntity.ok(user)
    }

    @GetMapping("/user/bounties")
    fun getUserBounties(principal: Principal): ResponseEntity<List<BountyView>> {
//        val githubUser = RequestUtil.getUserFromRequest(principal)
        val userBounties = bountyService.getUserBounties(0L)
        return ResponseEntity.ok(userBounties);
    }
}