package io.prhunter.api.user

import io.prhunter.api.RequestUtil
import io.prhunter.api.user.api.GithubUserView
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class UserController {

    @GetMapping("/user")
    fun getUser(principal: Principal): ResponseEntity<GithubUserView> {
        val user = RequestUtil.getUserFromRequest(principal).toView()
        return ResponseEntity.ok(user)
    }
}