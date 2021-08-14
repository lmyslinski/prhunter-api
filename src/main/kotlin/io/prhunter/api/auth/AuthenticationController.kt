package io.prhunter.api.auth

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
//import net.sentiwatch.api.user.UserProfile
//import net.sentiwatch.api.user.UserProfileRepository
//import net.sentiwatch.api.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
//import org.springframework.security.authentication.AuthenticationManager
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}
//
//@RestController()
//@RequestMapping("/auth")
//class AuthenticationController(
//    @Autowired val authenticationManager: AuthenticationManager,
//    @Autowired val jwtTokenProvider: JwtTokenProvider,
//    @Autowired val userProfileRepository: UserProfileRepository,
//    @Autowired val authService: AuthService,
//    private val userService: UserService,
//    private val objectMapper: ObjectMapper
//) {
//    @RequestMapping(value = ["/signin"], method = [RequestMethod.POST])
//    fun login(@RequestBody loginRequestDto: LoginRequestDto): ResponseEntity<*> {
//        try {
//            val authentication: Authentication = authenticationManager.authenticate(
//                UsernamePasswordAuthenticationToken(
//                    loginRequestDto.email,
//                    loginRequestDto.password
//                )
//            )
//
//            SecurityContextHolder.getContext().authentication = authentication
//            val token: String = jwtTokenProvider.generateToken(authentication)
//            val user: UserProfile = userProfileRepository.findByEmail(loginRequestDto.email).get()
//            return ResponseEntity.ok(
//                mapOf(
//                    Pair("token", token),
//                    Pair("firstName", user.firstName),
//                    Pair("lastName", user.lastName),
//                    Pair("subscription", user.activeSubscription),
//                    Pair("profileId", user.id)
//                )
//            )
//        } catch (ex: Throwable) {
//            return if (ex.message == "User is disabled") {
//                ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(objectMapper.writeValueAsString(mapOf("message" to "Your account is not active yet. Please check your email for an activation link.")))
//            } else if (ex.message == "Bad credentials") {
//                ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(objectMapper.writeValueAsString(mapOf("message" to "Sorry, looks like your credentials are invalid. Please check your data and try again.")))
//            } else {
//                ResponseEntity.badRequest().body(objectMapper.writeValueAsString(mapOf("message" to ex.message)))
//            }
//        }
//    }
//
//    @RequestMapping(value = ["/register"], method = [RequestMethod.POST])
//    fun register(@RequestBody registerUserDto: RegisterUserRequest): ResponseEntity<*> {
//        return try {
//            val newProfile = authService.registerAccount(registerUserDto)
//            log.info("Registered new user: ${newProfile.email}")
//            return ResponseEntity.ok("Registered ${newProfile.email}")
//        } catch (ex: Throwable) {
//            log.error { ex.message }
//            ResponseEntity.badRequest().body("Account registration failed: ${ex.message}")
//        }
//    }
//
//    @GetMapping("/confirm-account")
//    fun confirmAccount(@RequestParam token: String?): ResponseEntity<*> {
//        return try {
//            val newProfile = userService.confirmUserAccount(token)
//            log.info("Activated user account: ${newProfile.email}")
//            return ResponseEntity.ok("Activated user account: ${newProfile.email}")
//        } catch (ex: Throwable) {
//            log.error { ex.message }
//            ResponseEntity.badRequest().body("Account activation failed: ${ex.message}")
//        }
//    }
//}
//
//
