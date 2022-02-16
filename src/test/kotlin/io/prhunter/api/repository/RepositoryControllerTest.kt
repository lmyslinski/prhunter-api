package io.prhunter.api.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.prhunter.api.TestDataProvider
import io.prhunter.api.github.GithubService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.RepositoryList
import io.prhunter.api.installation.Installation
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.UserAccount
import io.prhunter.api.user.UserAccountRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RepositoryControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val installationService: InstallationService,
    @Autowired val userAccountRepository: UserAccountRepository
) {

    private val testInstallation = Installation(1329L, 22L, "user", 123L, "owner")

    @MockkBean
    private val githubService: GithubService? = null

    @Test
    fun `should return an empty repo list if user has no github account linked`(){
        TestDataProvider.setAuthenticatedContext()
        userAccountRepository.save(UserAccount(TestDataProvider.TEST_USER.id ))

        val response = mockMvc.get("/repo") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<List<GHRepoData>>(response)
        Assertions.assertTrue(actual.isEmpty())
    }

    @Test
    fun `should list repositories for user installations`() {
        TestDataProvider.setAuthenticatedContext()
        userAccountRepository.save(UserAccount(TestDataProvider.TEST_USER.id, 123L, "gh-token"))
        val newInstall = installationService.registerInstallation(testInstallation)
        val repolist = listOf(GHRepoData(132L, "test-repo", "full-repo-name", false))
        coEvery { githubService!!.listRepositories(newInstall.id) }.returns(RepositoryList(1, repolist))

        val response = mockMvc.get("/repo") {
            accept = MediaType.APPLICATION_JSON
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