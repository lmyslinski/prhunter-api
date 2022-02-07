package io.prhunter.api.user

import io.prhunter.api.RequestUtil
import io.prhunter.api.auth.FirebaseService
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.common.errors.EmptyInputException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.validation.Valid

@RestController
class UserController(
    val bountyService: BountyService,
    val userAccountService: UserAccountService
) {

    @GetMapping("/user")
    fun getUser(principal: Principal): ResponseEntity<UserAccountView> {
        val user = RequestUtil.getUserFromRequest(principal)
        val userView = userAccountService.getUserAccountView(user)
        return ResponseEntity.ok(userView)
    }

    @GetMapping("/user/bounties")
    fun getUserBounties(principal: Principal): ResponseEntity<List<BountyView>> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        val userBounties = bountyService.getUserBounties(firebaseUser.id)
        return ResponseEntity.ok(userBounties)
    }

    @GetMapping("/user/completed")
    fun getUserCompletedBounties(principal: Principal): ResponseEntity<List<BountyView>> {
        val firebaseUser = RequestUtil.getUserFromRequest(principal)
        val userBounties = bountyService.getCompletedBy(firebaseUser.id)
        return ResponseEntity.ok(userBounties)
    }

    @PutMapping("/user")
    fun updateUserDetails(@Valid @RequestBody updateUserAccount: UpdateUserAccount, principal: Principal): ResponseEntity<String> {
        val user = RequestUtil.getUserFromRequest(principal)
        if(updateUserAccount.email.isNullOrEmpty() && updateUserAccount.ethWalletAddress.isNullOrEmpty()){
            throw EmptyInputException()
        }
        userAccountService.updateUserAccount(user, updateUserAccount)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("")
    }
}