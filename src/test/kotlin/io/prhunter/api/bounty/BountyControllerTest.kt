package io.prhunter.api.bounty

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.bounty.api.CreateBountyRequest
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
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

    @BeforeEach
    fun setup() {
        val now = Instant.now()
        val bounties = listOf(
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
        bountyRepository.saveAll(bounties)
    }

    @AfterEach
    fun teardown(){
        bountyRepository.deleteAll()
    }

    @Test
    fun `should create a new bounty if signed in and issue owner`() {
        val response = mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.user("test-user"))
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<Bounty>(response)

        Assertions.assertNotNull(actual.id)
        Assertions.assertEquals(createBountyRequest.title, actual.title)
    }

    @Test
    fun `should return 401 for create bounty if not signed in`(){
        mockMvc.post("/bounty") {
            content = objectMapper.writeValueAsString(createBountyRequest)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isEqualTo(HttpStatus.UNAUTHORIZED.value()) }
        }
    }

    @Test
    fun `should return 403 for create bounty if not issue owner`(){
//        mockMvc.post("/bounty") {
//            content = objectMapper.writeValueAsString(createBountyRequest)
//            contentType = MediaType.APPLICATION_JSON
//            accept = MediaType.APPLICATION_JSON
//        }.andExpect {
//            status { isEqualTo(HttpStatus.UNAUTHORIZED.value()) }
//        }
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

        val resorted =  actual.sortedByDescending { it.updatedAt }
        Assertions.assertEquals(actual.first(), resorted.first())
        Assertions.assertEquals(actual.last(), resorted.last())
    }

    @Test
    fun `should get a single bounty successfully`(){
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

    // should list all bounties without any permissions
    // should get a single bounty without any permissions
    // should allow creating a bounty only for issue owners
    // should allow updating a bounty only for issue owners

}