package io.prhunter.api.bounty

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import io.prhunter.api.TestDataProvider
import io.prhunter.api.auth.AuthService
import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import io.prhunter.api.crypto.CoinGeckoApiService
import io.prhunter.api.github.auth.GithubToken
import io.prhunter.api.github.auth.GithubTokenRepository
import io.prhunter.api.github.client.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BountyControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val bountyRepository: BountyRepository,
    @Autowired val githubTokenRepository: GithubTokenRepository
) {

    private val createBountyRequest = CreateBountyRequest(
        "test-org",
        "test-repo",
        1234,
        "test-body",
        "statement", "acceptance",
        listOf("scala", "kotlin"),
        listOf("new", "first"),
        Experience.Beginner,
        BountyType.Feature,
        BigDecimal.valueOf(100L),
        "USD"
    )
    private val updateBountyRequest = UpdateBountyRequest(
        "new-title",
        "statement-new", "acceptance-new",
        listOf("kotlin", "javascript"),
        listOf("updated"),
        Experience.Advanced,
        BountyType.Feature,
        BigDecimal.valueOf(99),
        "ETH"
    )

    @MockkBean
    private val githubRestClient: GithubRestClient? = null

    @MockkBean
    private val coinGeckoApiService: CoinGeckoApiService? = null

    @MockkBean
    private val authService: AuthService? = null

    @BeforeEach
    fun setup() {
        bountyRepository.saveAll(TestDataProvider.BOUNTIES)
        githubTokenRepository.save(GithubToken(TestDataProvider.TEST_USER.id, 1L, "gh-token"))
        every { coinGeckoApiService!!.getCurrentEthUsdPrice() }.returns(BigDecimal.ONE)
    }

    @AfterEach
    fun teardown() {
        bountyRepository.deleteAll()
    }

    @Test
    fun `should create a new bounty if signed in and issue owner`() {
        TestDataProvider.setAuthenticatedContext()
        coEvery { githubRestClient!!.getRepository(any(), any(), any()) }.returns(
            GHRepoData(
                1L,
                "test-name",
                "full-name",
                false
            )
        )
        coEvery { githubRestClient!!.getIssue(any(), any(), any(), any()) }.returns(
            Issue(
                1L,
                "test-name",
                "full-name",
                "state",
                "body",
                0L
            )
        )

        val response = mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<BountyView>(response)

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
        TestDataProvider.setAuthenticatedContext()
        coEvery { githubRestClient!!.getRepository(any(), any(), any()) }.returns(
            GHRepoData(
                1L,
                "test-name",
                "full-name",
                false
            )
        )
        coEvery {
            githubRestClient!!.getIssue(
                any(),
                any(),
                any(),
                any()
            )
        }.throws(RuntimeException("Could not get issue"))

        mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.FORBIDDEN.value()) }
        }
    }

    @Test
    fun `should return 403 for create bounty if not repo owner`() {
        TestDataProvider.setAuthenticatedContext()
        coEvery {
            githubRestClient!!.getRepository(
                any(),
                any(),
                any()
            )
        }.throws(RuntimeException("Could not get repository"))

        mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.FORBIDDEN.value()) }
        }
    }

    @Test
    fun `should return 404 if bounty not found`() {
        mockMvc.get("/bounty/111").andExpect {
            status {
                isNotFound()
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
            }
        }
    }

    // TODO FIX POST MVP
    @Test()
    @Disabled
    fun `should list all bounties sorted by updated at desc`() {
        val response = mockMvc.get("/bounty").andExpect {
            status {
                isOk()
                content { contentType(MediaType.APPLICATION_JSON) }
            }
        }.andReturn()
        val actual: List<BountyView> = objectMapper.readValue(response.response.contentAsString)
        Assertions.assertEquals(3, actual.size)

        val resorted = actual.sortedByDescending { it.updatedAt }
        Assertions.assertEquals(actual.first(), resorted.first())
        Assertions.assertEquals(actual.last(), resorted.last())
    }

//     TODO FIX POST MVP
    @Test
    @Disabled
    fun `should get a single bounty successfully`() {
        val expected = bountyRepository.findAll().sortedBy { it.updatedAt }.first().toView(BigDecimal.ZERO)
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
        val differentUser = FirebaseUser("333", "aa", "bb")
        TestDataProvider.setAuthenticatedContext(differentUser)
        val expected = bountyRepository.findAll().sortedBy { it.updatedAt }.first()
        coEvery { githubRestClient!!.listAuthenticatedUserRepos(any()) }.returns(listOf())

        mockMvc.put("/bounty/${expected.id}") {
            content = objectMapper.writeValueAsString(updateBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.FORBIDDEN.value()) }
        }
    }

    // TODO FIX POST MVP
    @Test
    @Disabled
    fun `should update bounty successfully`() {
        val before = bountyRepository.findAll().sortedBy { it.updatedAt }.first()

        coEvery { githubRestClient!!.listAuthenticatedUserRepos(any()) }.returns(
            listOf(
                GHRepoPermissionData(
                    before.repoId,
                    "",
                    "",
                    false,
                    Permissions(true, true, true, true, true)
                )
            )
        )

        mockMvc.put("/bounty/${before.id}") {
            content = objectMapper.writeValueAsString(updateBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.NO_CONTENT.value()) }
        }

        val after = bountyRepository.findById(before.id!!).get().toView(BigDecimal.ZERO)
        Assertions.assertEquals(before.id, after.id)
        Assertions.assertEquals(before.issueId, after.issueId)
        Assertions.assertEquals(before.repoId, after.repoId)
        Assertions.assertEquals(before.createdAt, after.createdAt)

        Assertions.assertEquals(updateBountyRequest.problemStatement, after.problemStatement)
        Assertions.assertEquals(updateBountyRequest.acceptanceCriteria, after.acceptanceCriteria)
        Assertions.assertEquals(updateBountyRequest.bountyValue, after.bountyValue)
        Assertions.assertEquals(updateBountyRequest.bountryCurrency, after.bountyCurrency)
        Assertions.assertEquals(updateBountyRequest.title, after.title)
        Assertions.assertArrayEquals(updateBountyRequest.languages.toTypedArray(), after.languages)

        Assertions.assertNotEquals(before.updatedAt, after.updatedAt)
    }
}