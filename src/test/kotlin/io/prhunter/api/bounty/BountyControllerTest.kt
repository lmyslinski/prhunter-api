package io.prhunter.api.bounty

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import io.prhunter.api.github.client.GHRepoPermissionData
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Permissions
import io.prhunter.api.installation.InstallationService
import io.prhunter.api.user.GithubUser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BountyControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val bountyRepository: BountyRepository
) {

    private val createBountyRequest = CreateBountyRequest(
        1L,
        2L,
        "test-issue",
        "test-body",
        listOf("scala", "kotlin"),
        BigDecimal.valueOf(100L),
        "USD"
    )
    private val updateBountyRequest = UpdateBountyRequest(
        "new-title",
        "new-desc",
        listOf("kotlin", "javascript"),
        BigDecimal.valueOf(99),
        "ETH"
    )

    private val now = Instant.now()
    private val bounties = listOf(
        Bounty(
            1L, 1L, 1L, "1", "1", arrayOf("scala"), BigDecimal.valueOf(10), "ETH", updatedAt = now.minus(
                1,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            2L, 2L, 2L, "2", "2", arrayOf("java"), BigDecimal.valueOf(20), "BTC", updatedAt = now.minus(
                2,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            3L, 3L, 3L, "3", "3", arrayOf("javascript"), BigDecimal.valueOf(30), "USD", updatedAt = now.minus(
                3,
                ChronoUnit.MINUTES
            )
        )
    )

    private val testUser = GithubUser(23L, "test-user", null, "Johny Cash", "tmp-token", Instant.now(), Instant.now())


    @MockkBean
    private val githubRestClient: GithubRestClient? = null

    @BeforeEach
    fun setup() {
        bountyRepository.saveAll(bounties)
    }

    @AfterEach
    fun teardown() {
        bountyRepository.deleteAll()
    }

    @Test
    fun `should create a new bounty if signed in and issue owner`() {
        coEvery { githubRestClient!!.listAuthenticatedUserRepos(any()) }.returns(
            listOf(
                GHRepoPermissionData(
                    createBountyRequest.repoId,
                    "",
                    "",
                    false,
                    Permissions(true, true, true)
                )
            )
        )

        val response = mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            with(oauth2Login().oauth2User(testUser))
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<Bounty>(response)

        Assertions.assertNotNull(actual.id)
        Assertions.assertEquals(createBountyRequest.title, actual.title)
    }

    @Test
    fun `should return 401 for create bounty if not signed in`() {
        mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.UNAUTHORIZED.value()) }
        }
    }

    @Test
    fun `should return 403 for create bounty if not issue owner`() {
        coEvery { githubRestClient!!.listAuthenticatedUserRepos(any()) }.returns(
            listOf(
                GHRepoPermissionData(
                    createBountyRequest.repoId,
                    "",
                    "",
                    false,
                    Permissions(false, true, true)
                )
            )
        )

        mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            with(oauth2Login().oauth2User(testUser))
        }.andExpect {
            status { isEqualTo(HttpStatus.FORBIDDEN.value()) }
        }
    }

    @Test
    fun `should list all bounties sorted by updated at desc`() {
        val response = mockMvc.get("/bounty").andExpect {
            status {
                isOk()
                content { contentType(MediaType.APPLICATION_JSON) }
            }
        }.andReturn()
        val actual: List<Bounty> = objectMapper.readValue(response.response.contentAsString)
        Assertions.assertEquals(3, actual.size)

        val resorted = actual.sortedByDescending { it.updatedAt }
        Assertions.assertEquals(actual.first(), resorted.first())
        Assertions.assertEquals(actual.last(), resorted.last())
    }

    @Test
    fun `should get a single bounty successfully`() {
        val expected = bountyRepository.findAll().sortedBy { it.updatedAt }.first()
        mockMvc.get("/bounty/${expected.id}").andExpect {
            status {
                isOk()
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                content { json(objectMapper.writeValueAsString(expected)) }
            }
        }
    }

    @Test
    fun `should return 401 for update bounty if not signed in`() {
        mockMvc.put("/bounty/1") {
            content = objectMapper.writeValueAsString(updateBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.UNAUTHORIZED.value()) }
        }
    }

    @Test
    fun `should return 403 for update bounty if not issue owner`() {
        val expected = bountyRepository.findAll().sortedBy { it.updatedAt }.first()
        coEvery { githubRestClient!!.listAuthenticatedUserRepos(any()) }.returns(listOf())

        mockMvc.put("/bounty/${expected.id}") {
            content = objectMapper.writeValueAsString(updateBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            with(oauth2Login().oauth2User(testUser))
        }.andExpect {
            status { isEqualTo(HttpStatus.FORBIDDEN.value()) }
        }
    }

    @Test
    fun `should update bounty successfully`() {
        val before = bountyRepository.findAll().sortedBy { it.updatedAt }.first()

        coEvery { githubRestClient!!.listAuthenticatedUserRepos(any()) }.returns(
            listOf(
                GHRepoPermissionData(
                    before.repoId,
                    "",
                    "",
                    false,
                    Permissions(true, true, true)
                )
            )
        )

        mockMvc.put("/bounty/${before.id}") {
            content = objectMapper.writeValueAsString(updateBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            with(oauth2Login().oauth2User(testUser))
        }.andExpect {
            status { isEqualTo(HttpStatus.NO_CONTENT.value()) }
        }

        val after = bountyRepository.findById(before.id!!).get()
        Assertions.assertEquals(before.id, after.id)
        Assertions.assertEquals(before.issueId, after.issueId)
        Assertions.assertEquals(before.repoId, after.repoId)
        Assertions.assertEquals(before.createdAt, after.createdAt)

        Assertions.assertEquals(updateBountyRequest.body, after.body)
        Assertions.assertEquals(updateBountyRequest.bountyValue, after.bountyValue)
        Assertions.assertEquals(updateBountyRequest.bountryCurrency, after.bountyCurrency)
        Assertions.assertEquals(updateBountyRequest.title, after.title)
        Assertions.assertArrayEquals(updateBountyRequest.languages.toTypedArray(), after.languages)

        Assertions.assertNotEquals(before.updatedAt, after.updatedAt)
    }
}