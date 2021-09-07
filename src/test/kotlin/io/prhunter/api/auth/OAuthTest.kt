package io.prhunter.api.auth

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.prhunter.api.OAuthContextInitializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [OAuthContextInitializer::class])
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

    private fun stubUserInfoResponse(){
        wireMockServer.stubFor(
            get(urlPathMatching("/userinfo"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(

                            ("{\"sub\":\"111\"" +
                                    ",\"login\":\"Mark Hoogenboom\"" +
                                    ",\"created_at\":\"2008-01-14T04:33:35Z\"" +
                                    ",\"id\":123" +
                                    ", \"email\":\"mark.hoogenboom@example.com\"" +
                                    "}")
                        )
                )
        )
    }

    private fun stubTokenResponse(){
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
    }

    @Test
    fun `should fill in custom state when oauth triggered from install`(){
        stubTokenResponse()
        stubUserInfoResponse()
        mockMvc.get("/login/oauth2/code/github?code=github&setup_action=install").andExpect {
            status { is3xxRedirection() }
            redirectedUrl("https://prhunter.io/signup-success")
        }
    }

    @Test()
    @Disabled
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

//        // TODO this has to be done differently
//        val redirectResult = mockMvc.get("/oauth2/authorization/github").andExpect {
//            status { is3xxRedirection() }
//        }.andReturn()
//        val target = redirectResult.response.getHeader("Location")!!
//        mockMvc.get(target).andExpect {
//
//        }
    }
}