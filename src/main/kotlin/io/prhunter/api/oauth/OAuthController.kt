package io.prhunter.api.oauth

import io.prhunter.api.user.GithubUserService
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}
//
//@RestController
//@RequestMapping("/oauth")
//class OAuthController(
//    private val userService: GithubUserService
//) {
//    @GetMapping("/singup")
//    fun handleWebhook(@RequestParam code: String): ResponseEntity<String> {
//        return runBlocking{
//            log.info { "/oauth/signup was called" }
//            try{
//                userService.registerUser(code)
//                ResponseEntity.ok().body("")
//            } catch (ex: Throwable){
//                log.error { ex }
//                ResponseEntity.internalServerError().body("Sorry, but the registration process has failed. Please remove the app from you account and try again.")
//            }
//        }
//    }
//
//    @GetMapping("/complete")
//    fun callback(): ResponseEntity<String> {
//        log.info { "/complete was called" }
//        log.info { "Callback called" }
//        return ResponseEntity.ok().body("OAuth setup completed")
//    }
//}