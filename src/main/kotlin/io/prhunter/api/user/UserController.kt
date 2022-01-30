package io.prhunter.api.user

import com.google.firebase.auth.UserRecord
import io.prhunter.api.RequestUtil
import io.prhunter.api.auth.AuthService
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.api.BountyView
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class UserController(
    val bountyService: BountyService,
    val authService: AuthService
) {

    @GetMapping("/user")
    fun getUser(principal: Principal): ResponseEntity<UserRecord> {
        val user = RequestUtil.getUserFromRequest(principal)
        val userRecord = authService.getUserById(user.id)
        return ResponseEntity.ok(userRecord)
    }

    @GetMapping("/user/bounties")
    fun getUserBounties(principal: Principal): ResponseEntity<List<BountyView>> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        val userBounties = bountyService.getUserBounties(firebaseUser.id)
        return ResponseEntity.ok(userBounties);
    }

    @GetMapping("/user/completed")
    fun getUserCompletedBounties(principal: Principal): ResponseEntity<List<BountyView>> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        val userBounties = bountyService.getCompletedBy(firebaseUser.id)
        return ResponseEntity.ok(userBounties);
    }

    @PutMapping
    fun updateUserDetails(){

    }
}