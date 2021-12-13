package io.prhunter.api.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.prhunter.api.github.GithubAppInstallationService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.RepositoryList
import io.prhunter.api.installation.Installation
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RepositoryControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val installationService: InstallationService
) {

    private val testUser = User(23L, "test-user", null, "Johny Cash", "tmp-token", Instant.now(), Instant.now())
    private val testInstallation = Installation(1329L, 22L, "user", testUser.id, "owner")

    @MockkBean
    private val appInstallationService: GithubAppInstallationService? = null

    @Test
    fun `should list repositories for user installations`() {
        val newInstall = installationService.registerInstallation(testInstallation)
        val repolist = listOf(GHRepoData(132L, "test-repo", "full-repo-name", false))
        coEvery { appInstallationService!!.listRepositories(newInstall.id) }.returns(RepositoryList(1, repolist))

        val response = mockMvc.get("/repo") {
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(testUser))
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<List<GHRepoData>>(response)
        Assertions.assertEquals(repolist, actual)
    }

    @Test
    fun `should return 401 if not signed in`(){
        mockMvc.get("/repo") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.UNAUTHORIZED.value()) }
        }
    }
}