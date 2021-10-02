package io.prhunter.api.github.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.github.webhooks.model.AccountDetails
import io.prhunter.api.github.webhooks.model.WebhookBody
import io.prhunter.api.github.webhooks.model.InstallationDetails
import io.prhunter.api.installation.Installation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
internal class WebhookControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) {

    @MockkBean
    private val installationService: InstallationService? = null

    @Test
    fun `should register an app`() {
        every { installationService!!.registerInstallation(any())}.returns(mockk())
        val input = WebhookBody(InstallationDetails(1L, AccountDetails(2L, "User")), AccountDetails(3L, "Organisation"), "created")
        mockMvc.post("/webhook") {
            content = objectMapper.writeValueAsString(input)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify { installationService!!.registerInstallation(any()) }
    }

    @Test
    fun `should delete an app`(){
        every { installationService!!.removeInstallation(any())} just Runs
        val input = WebhookBody(InstallationDetails(1L, AccountDetails(2L, "User")), AccountDetails(3L, "Organisation"), "deleted")
        mockMvc.post("/webhook") {
            content = objectMapper.writeValueAsString(input)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify { installationService!!.removeInstallation(any()) }
    }

}