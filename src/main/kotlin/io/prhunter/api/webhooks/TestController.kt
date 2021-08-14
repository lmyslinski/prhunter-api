package io.prhunter.api.webhooks

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@RestController
class TestController {

    @GetMapping("/")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok().body("Successfully logged in")
    }

}


