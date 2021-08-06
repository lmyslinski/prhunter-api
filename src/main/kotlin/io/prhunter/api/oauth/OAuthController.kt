package io.prhunter.api.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/oauth")
class OAuthController(
    private val githubSecrets: GithubSecrets,
    private val githubClient: GithubClient,
    private val objectMapper: ObjectMapper,
) {
    @GetMapping("/install")
    fun handleWebhook(@RequestParam code: String): ResponseEntity<String> {
        log.info { "Oauth request: $code" }
        val resp = runBlocking {
            githubClient.getAccessToken(
                AccessTokenRequest(
                    code, githubSecrets.clientId, githubSecrets.clientSecret, "https://15ab75995c2d.ngrok.io/api/oauth/complete"
                ),
            )
        }.execute()
        if(resp.isSuccessful){
            println(resp.body())
            val obj = objectMapper.readValue(resp.body(), AccessTokenResponse::class.java)
            println(obj)
        }
        return ResponseEntity.ok().body("")
    }

    @GetMapping("/complete")
    fun callback(): ResponseEntity<String> {
        log.info { "Callback called" }
        return ResponseEntity.ok().body("OAuth setup completed")
    }
}