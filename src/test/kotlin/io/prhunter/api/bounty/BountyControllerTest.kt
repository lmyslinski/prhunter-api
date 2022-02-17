package io.prhunter.api.bounty

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import io.prhunter.api.TestDataProvider
import io.prhunter.api.TestDataProvider.BOUNTIES
import io.prhunter.api.TestDataProvider.TEST_USER
import io.prhunter.api.auth.FirebaseService
import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import io.prhunter.api.crypto.CoinGeckoApiService
import io.prhunter.api.github.client.GHRepoData
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.Issue
import io.prhunter.api.user.UserAccount
import io.prhunter.api.user.UserAccountRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import java.time.Instant
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BountyControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val bountyRepository: BountyRepository,
    @Autowired val bountyService: BountyService,
    @Autowired val userAccountRepository: UserAccountRepository
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
        "ETH",
        Instant.now().plusSeconds(100L).epochSecond
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
    private val firebaseService: FirebaseService? = null

    @BeforeEach
    fun setup() {
        bountyRepository.saveAll(BOUNTIES)
        userAccountRepository.save(UserAccount(TEST_USER.id, 1L, "gh-token"))
        every { coinGeckoApiService!!.getCurrentPrice(any()) }.returns(BigDecimal.ONE)
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
                5L,
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
    fun `should accept create bounty retry and return existing bounty`() {
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
                5L,
                "test-name",
                "full-name",
                "state",
                "body",
                0L
            )
        )

        mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }

        val duplicateResponse = mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<BountyView>(duplicateResponse)

        Assertions.assertNotNull(actual.id)
        Assertions.assertEquals(createBountyRequest.title, actual.title)
        Assertions.assertEquals(bountyRepository.findAll().size, BOUNTIES.size+1)
    }


    @Test
    fun `should return 400 if bounty already exists for an issue`() {
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
        val bounty = bountyRepository.findByIssueId(1L)
        bounty!!.bountyStatus = BountyStatus.ACTIVE
        bountyRepository.save(bounty)

        mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is4xxClientError() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        // need to figure out localdatetime deserialization
//        val apiError: ApiError = objectMapper.readValue(response)
//        Assertions.assertEquals("This issue already has a bounty. You cannot have multiple bounties for the same issue.", apiError.message)
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
        mockMvc.get("/bounty/${UUID.randomUUID()}").andExpect {
            status {
                isNotFound()
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
            }
        }
    }

    @Test
    fun `should list all bounties sorted by created at desc`() {
        val response = mockMvc.get("/bounty").andExpect {
            status {
                isOk()
                content { contentType(MediaType.APPLICATION_JSON) }
            }
        }.andReturn()
        val actual: List<BountyView> = objectMapper.readValue(response.response.contentAsString)
        Assertions.assertEquals(BOUNTIES.size, actual.size)

        val resorted = actual.sortedByDescending { it.createdAt }
        Assertions.assertEquals(actual.first(), resorted.first())
        Assertions.assertEquals(actual.last(), resorted.last())
    }

    @Test
    fun `should get a single bounty successfully`() {
        val expected = bountyRepository.findAll().sortedBy { it.createdAt }.first()
        mockMvc.get("/bounty/${expected.id}").andExpect {
            status {
                isOk()
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json((objectMapper.writeValueAsString(bountyService.toView((expected)))))
                }
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
        val expected = bountyRepository.findAll().sortedBy { it.createdAt }.first()
        coEvery { githubRestClient!!.listAuthenticatedUserRepos(any()) }.returns(listOf())

        mockMvc.put("/bounty/${expected.id}") {
            content = objectMapper.writeValueAsString(updateBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.FORBIDDEN.value()) }
        }
    }
}