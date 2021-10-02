package io.prhunter.api.github.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.ktor.client.*
import io.prhunter.api.WireMockContextInitializer
import io.prhunter.api.config.JacksonConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@ActiveProfiles("test")
@AutoConfigureMockMvc
class GithubRestClientTest {

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @LocalServerPort
    private val serverPort = 0

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }

    @Test
    fun `should list repositories for an installation`(){
        val repoListResponse = ClassPathResource("/repository-list-response.json").file.readText()
        val token = "12345"

        wireMockServer.stubFor(
            get("/installation/repositories").withHeader("Authorization", equalTo("Bearer $token")).willReturn(aResponse().withBody(repoListResponse))
        )

        val client = GithubRestClient(HttpClient(), JacksonConfig().objectMapper(), wireMockServer.baseUrl())
        val resp = runBlocking {
            client.listRepositories("12345")
        }
        val expected = RepositoryList(
            1L,
            listOf(GHRepoData(1296269L, "Hello-World", "octocat/Hello-World", false))
        )
        Assertions.assertEquals(expected, resp)
    }

    @Test
    fun `should get issue list correctly for a repository`(){
        val issueListResponse = ClassPathResource("/issue-list-response.json").file.readText()
        val owner = "123"
        val repo = "test-repo"
        val token = "12345"

        wireMockServer.stubFor(
            get("/repos/$owner/$repo/issues").withHeader("Authorization", equalTo("Bearer $token")).willReturn(aResponse().withBody(issueListResponse))
        )

        val client = GithubRestClient(HttpClient(), JacksonConfig().objectMapper(), wireMockServer.baseUrl())
        val resp = runBlocking {
            client.listIssues(owner, repo, token)
        }

        val expected = listOf(
            Issue(1L, "MDU6SXNzdWUx", "Found a bug", "open", "I'm having a problem with this.", 1347)
        )
        Assertions.assertEquals(expected, resp)
    }

    @Test
    fun `should list user repository list correctly`(){
        val userRepoListResponse = ClassPathResource("/user-repo-list-response.json").file.readText()
        val token = "12345"
        wireMockServer.stubFor(
            get("/user/repos").withHeader("Authorization", equalTo("Bearer $token")).willReturn(aResponse().withBody(userRepoListResponse))
        )

        val client = GithubRestClient(HttpClient(), JacksonConfig().objectMapper(), wireMockServer.baseUrl())
        val resp = runBlocking {
            client.listAuthenticatedUserRepos(token)
        }

        val expected = GHRepoPermissionData(302553476, "githubactions-modeldeployment-demo-githubalgo", "algorithmiaio/githubactions-modeldeployment-demo-githubalgo", false, Permissions(false, false,false, false, true))
        Assertions.assertEquals(expected, resp.first())
    }
}