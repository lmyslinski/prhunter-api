package io.prhunter.api.webhooks

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
) {

    @Test
    fun handleWebhook() {
        val input = "12345"
        mockMvc.post("/webhook") {
            content = input
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { string(input) }
        }
    }
}