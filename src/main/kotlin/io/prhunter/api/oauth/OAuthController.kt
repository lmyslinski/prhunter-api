package io.prhunter.api.oauth

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/oauth")
class OAuthController(
    @Value("\${github.clientSecret}") private val clientSecret: String,
    private val githubClient: GithubClient
) {
    @GetMapping("/install")
    fun handleWebhook(@RequestParam code: String): ResponseEntity<String> {
        log.info { "Oauth request: $code" }
        val resp = runBlocking {
            githubClient.getAccessToken(
                AccessTokenRequest(
                    code, "Iv1.339cb2b3104333db", clientSecret, "https://smee.io/TdZaUekAVfbZdxcv/api/oauth/complete"
                ),
            ).string()
        }
        println(resp)
        return ResponseEntity.ok().body("")
    }

    @GetMapping("/complete")
    fun callback(): ResponseEntity<String> {
        log.info { "Callback called" }
        return ResponseEntity.ok().body("OAuth setup completed")
    }
}