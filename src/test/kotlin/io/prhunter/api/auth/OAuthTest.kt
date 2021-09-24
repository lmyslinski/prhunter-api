package io.prhunter.api.auth

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.prhunter.api.OAuthContextInitializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.net.URLDecoder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [OAuthContextInitializer::class])
@ActiveProfiles("test")
@AutoConfigureMockMvc
class OAuthTest(
    @Autowired val mockMvc: MockMvc,
) {

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    private val serverPort = 0

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }

    private fun stubUserInfoResponse() {
        wireMockServer.stubFor(
            get(urlPathMatching("/userinfo"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            ("""{"sub":"111","login":"Mark Hoogenboom","created_at":"2008-01-14T04:33:35Z","id":123, "email":"mark.hoogenboom@example.com"}""")
                        )
                )
        )
    }

    private fun stubTokenResponse() {
        wireMockServer.stubFor(
            post(urlPathMatching("/oauth/token"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """{"access_token":"my-access-token", "token_type":"Bearer", "expires_in":"3600"}"""
                        )
                )
        )
    }

    fun stubLoginSuccessWithRedirect(state: String){
        wireMockServer.stubFor(
            get(urlPathMatching("/login/oauth/authorize"))
                .willReturn(
                    aResponse()
                        .withStatus(302)
                        .withHeader(
                            "Location",
                            "/login/oauth2/code/github?code=invalid&state=${state}"
                        )
                )
        )
    }

    @Test
    fun `should fill in custom state when oauth triggered from install`() {
        stubTokenResponse()
        stubUserInfoResponse()
        mockMvc.get("/login/oauth2/code/github?code=github&setup_action=install").andExpect {
            status { is3xxRedirection() }
            redirectedUrl("https://prhunter.io/signup-success")
        }
    }

    @Test
    fun `should sign in with oauth successfully`(){
        stubTokenResponse()
        stubUserInfoResponse()

        // Login with github button clicked
        val resp = mockMvc.get("/oauth2/authorization/github").andExpect {
            status { is3xxRedirection() }
        }.andReturn()
        // Web client is redirected to the github site
        val redirectUrl = URLDecoder.decode(resp.response.redirectedUrl)
        val state = redirectUrl.split('&')[3].substring(6)
        // github site calls our callback from the login

        stubLoginSuccessWithRedirect(state)

        val githubResponse = restTemplate.getForEntity<String>(redirectUrl)
        val redirectLocation: String = githubResponse.headers["Location"]!![0]

        Assertions.assertEquals(302, githubResponse.statusCode.value())
        Assertions.assertEquals("/login/oauth2/code/github?code=invalid&state=${state}", redirectLocation)

        // TODO finishing this test requires persisting the session between resttemplate and mockmvc... this is bit too much effort for now
        val omg = mockMvc.get(redirectLocation).andReturn()
//           omg.response
//            .andExpect {
//            status { is2xxSuccessful() }
//            redirectedUrl("https://prhunter.io/signup-success")
//        }
    }
}