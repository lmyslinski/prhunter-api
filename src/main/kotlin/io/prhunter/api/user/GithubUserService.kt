package io.prhunter.api.user

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.prhunter.api.oauth.AccessTokenRequest
import io.prhunter.api.oauth.AccessTokenResponse
import io.prhunter.api.oauth.GithubSecrets
import org.kohsuke.github.GitHubBuilder
import org.springframework.stereotype.Service

@Service
class GithubUserService(
    private val githubUserRepository: GithubUserRepository,
    private val githubSecrets: GithubSecrets,
    private val httpClient: HttpClient,
) {

    suspend fun registerUser(code: String): GithubUser {
        val accessToken = getAccessToken(code)
        val userProfile = GitHubBuilder().withOAuthToken(accessToken.accessToken).build().myself
        val newUser = GithubUser(
            userProfile.id,
            userProfile.login,
            userProfile.email,
            userProfile.name,
            accessToken.accessToken,
            userProfile.createdAt.toInstant()
        )
        return githubUserRepository.save(newUser)
    }

    private suspend fun getAccessToken(code: String): AccessTokenResponse {
        return httpClient.post("https://github.com/login/oauth/access_token") {
            contentType(ContentType.Application.Json)
            accept(ContentType.parse("application/vnd.github.v3+json"))
            body = AccessTokenRequest(
                code,
                githubSecrets.clientId,
                githubSecrets.clientSecret,
                "http://localhost:3000/signup-success"
            )
        }
    }
}