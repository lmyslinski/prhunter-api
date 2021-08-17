package io.prhunter.api.auth

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.common.LocalNotifier.notifier
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.UrlPattern
import io.prhunter.api.WireMockContextInitializer
import org.junit.ClassRule
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [WireMockContextInitializer::class])

@ActiveProfiles("test")
@AutoConfigureMockMvc
class OAuthTest(
    @Autowired val mockMvc: MockMvc,
) {

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @LocalServerPort
    private val serverPort = 0

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }
    @Test
    fun stopOAuthResponses() {
        wireMockServer.stubFor(
            get(urlPathMatching("/oauth/authorize.*"))
                .willReturn(
                    aResponse()
                        .withStatus(302)
                        .withHeader(
                            "Location",
                            "http://localhost:${serverPort}/login/oauth2/code/my-oauth-client?code=my-acccess-code&state=\${state}"
                        )
                        .withTransformers("CaptureStateTransformer")
                )
        )
        wireMockServer.stubFor(
            post(urlPathMatching("/oauth/token"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            "{\"access_token\":\"my-access-token\"" +
                                    ", \"token_type\":\"Bearer\"" +
                                    ", \"expires_in\":\"3600\"" +
                                    "}"
                        )
                )
        )
        wireMockServer.stubFor(
            get(urlPathMatching("/userinfo"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            ("{\"sub\":\"my-user-id\"" +
                                    ",\"name\":\"Mark Hoogenboom\"" +
                                    ", \"email\":\"mark.hoogenboom@example.com\"" +
                                    "}")
                        )
                )
        )
        val redirectResult = mockMvc.get("/oauth2/authorization/github").andExpect {
            status { is3xxRedirection() }
        }.andReturn()
        val target = redirectResult.response.getHeader("Location")!!
        mockMvc.get(target).andExpect {

        }
    }
}