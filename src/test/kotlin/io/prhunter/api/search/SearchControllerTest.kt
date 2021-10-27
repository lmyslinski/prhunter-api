package io.prhunter.api.search

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.bounty.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SearchControllerTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var bountyRepository: BountyRepository

    @BeforeEach
    fun setup() {
        bountyRepository.saveAll(bounties)
    }

    @AfterEach
    fun teardown() {
        bountyRepository.deleteAll()
    }

    private val now = Instant.now()
    private val bounties = listOf(
        Bounty(
            1L, 1L, 1L, "test", "1", arrayOf("scala"), tags = arrayOf("new", "first"),
            Experience.Beginner,
            BountyType.Bug, BigDecimal.valueOf(10), "ETH", updatedAt = now.minus(
                1,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            2L, 2L, 2L, "2", "2", arrayOf("java"), tags = arrayOf("new", "second"),
            Experience.Advanced,
            BountyType.Housekeeping, BigDecimal.valueOf(20), "ETH", updatedAt = now.minus(
                4,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            3L,
            3L,
            3L,
            "3",
            "test in here",
            arrayOf("javascript"),
            tags = arrayOf("another", "react"),
            Experience.Beginner,
            BountyType.Feature,
            BigDecimal.valueOf(30),
            "ETH",
            updatedAt = now.minus(
                3,
                ChronoUnit.MINUTES
            )
        ),
        Bounty(
            4L,
            34L,
            4L,
            "4",
            "4",
            arrayOf("other"),
            tags = arrayOf("react", "ror"),
            Experience.Intermediate,
            BountyType.Meta,
            BigDecimal.valueOf(30),
            "USD",
            updatedAt = now.minus(
                2,
                ChronoUnit.MINUTES
            )
        )
    )

    @Test
    fun `should sort by updated at by default`() {
        val results = search(SearchRequest())
        Assertions.assertEquals(1, results.pageNumber)
        Assertions.assertEquals(4, results.total)
        // use issue id instead of id because it'd autoincrement
        Assertions.assertArrayEquals(arrayOf<Long>(1, 4, 3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by experience correctly`() {
        val results = search(SearchRequest(experience = Experience.Beginner))
        Assertions.assertEquals(2, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(1, 3), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by language correctly`() {
        val results = search(SearchRequest(language = "java"))
        Assertions.assertEquals(1, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by price and currency correctly`() {
        val results = search(
            SearchRequest(
                price = PriceFilterParams(
                    BigDecimal.valueOf(11L),
                    BigDecimal.valueOf(21L),
                    BountyCurrency.ETH
                )
            )
        )
        Assertions.assertEquals(1, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by max price only`() {
        val results = search(
            SearchRequest(
                price = PriceFilterParams(
                    to = BigDecimal.valueOf(21L),
                    currency = BountyCurrency.ETH
                )
            )
        )
        Assertions.assertEquals(2, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(1, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by min price only`() {
        val results = search(
            SearchRequest(
                price = PriceFilterParams(
                    min = BigDecimal.valueOf(11L),
                    currency = BountyCurrency.ETH
                )
            )
        )
        Assertions.assertEquals(2, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by currency only`() {
        val results = search(
            SearchRequest(
                price = PriceFilterParams(
                    currency = BountyCurrency.ETH
                )
            )
        )
        Assertions.assertEquals(3, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(1, 3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by tags correctly`() {
        val results = search(SearchRequest(tags = listOf("new")))
        Assertions.assertEquals(2, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(1, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by bounty type correctly`() {
        val results = search(SearchRequest(bountyType = BountyType.Feature))
        Assertions.assertEquals(1, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(3), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by title or body correctly`() {
        val results = search(SearchRequest(titleOrBody = "test"))
        Assertions.assertEquals(2, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(1,3), results.content.map { it.issueId }.toTypedArray())
    }

    private fun search(searchRequest: SearchRequest): PageResponse<Bounty> {
        val response = mockMvc.post("/bounty/search") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(searchRequest)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString
        return objectMapper.readValue(response)
    }
}