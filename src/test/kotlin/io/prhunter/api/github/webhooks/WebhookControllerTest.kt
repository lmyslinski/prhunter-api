package io.prhunter.api.github.webhooks

import com.ninjasquad.springmockk.MockkBean
import io.mockk.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebhookControllerTest(
    @Autowired val mockMvc: MockMvc,
) {

    @MockkBean
    private val installationHandler: InstallationHandler? = null

    @MockkBean
    private val pullRequestHandler: PullRequestHandler? = null

    @Test
    fun `should handle pull request opened request correctly`() {
        val pullRequestBody = ClassPathResource("/github/webhook/pull-request-opened.json").file.readText()
        coEvery { pullRequestHandler?.handleOpened(any()) } returns Unit
        mockMvc.post("/webhook") {
            content = pullRequestBody
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        coVerify(exactly = 1) { pullRequestHandler?.handleOpened(any()) }
        confirmVerified(pullRequestHandler!!)
    }

    @Test
    fun `should handle pull request closed request correctly`() {
        val pullRequestBody = ClassPathResource("/github/webhook/pull-request-closed.json").file.readText()
        mockMvc.post("/webhook") {
            content = pullRequestBody
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        coVerify { pullRequestHandler!! wasNot Called }
        confirmVerified(pullRequestHandler!!)
    }

    @Test
    fun `should handle pull request merged request correctly`() {
        val pullRequestBody = ClassPathResource("/github/webhook/pull-request-merged.json").file.readText()
        coEvery { pullRequestHandler?.handleMerged(any()) } returns Unit
        mockMvc.post("/webhook") {
            content = pullRequestBody
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        coVerify(exactly = 1) { pullRequestHandler?.handleMerged(any()) }
        confirmVerified(pullRequestHandler!!)
    }

    @Test
    fun `should handle installation created request correctly`() {
        val installationRequest = ClassPathResource("/github/webhook/installation-created.json").file.readText()
        every { installationHandler?.handle(any()) } returns Unit
        mockMvc.post("/webhook") {
            content = installationRequest
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        coVerify(exactly = 1) { installationHandler?.handle(any()) }
        confirmVerified(installationHandler!!)
    }

    @Test
    fun `should handle installation deleted request correctly`() {
        val installationRequest = ClassPathResource("/github/webhook/installation-deleted.json").file.readText()
        every { installationHandler?.handle(any()) } returns Unit
        mockMvc.post("/webhook") {
            content = installationRequest
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        coVerify(exactly = 1) { installationHandler?.handle(any()) }
        confirmVerified(installationHandler!!)
    }

    @Test
    fun `should handle issue edited request successfully`(){
        val issueRequest = ClassPathResource("/github/webhook/issue-edited.json").file.readText()
        mockMvc.post("/webhook") {
            content = issueRequest
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
        // we don't have issue handling for now so just ignore it
    }

}