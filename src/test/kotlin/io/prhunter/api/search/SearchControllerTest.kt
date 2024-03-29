package io.prhunter.api.search

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.TestDataProvider
import io.prhunter.api.bounty.*
import io.prhunter.api.crypto.CryptoCurrency
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.math.BigDecimal

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
        bountyRepository.saveAll(TestDataProvider.BOUNTIES)
    }

    @AfterEach
    fun teardown() {
        bountyRepository.deleteAll()
    }

    @Test
    fun `should sort by created at by default`() {
        val results = search(SearchRequest())
        Assertions.assertEquals(1, results.pageNumber)
        Assertions.assertEquals(4, results.total)
        // use issue id instead of id because UUID and shit
        Assertions.assertArrayEquals(arrayOf<Long>(1, 4, 3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by experience correctly`() {
        val results = search(SearchRequest(experience = Experience.Beginner))
        Assertions.assertArrayEquals(arrayOf<Long>(1, 3, 2), results.content.map { it.issueId }.toTypedArray())
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
                ),
                currency = CryptoCurrency.ETH
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
                )
            )
        )
        Assertions.assertArrayEquals(arrayOf<Long>(4, 3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by currency only`() {
        val results = search(
            SearchRequest(
                currency = CryptoCurrency.ETH
            )
        )
        Assertions.assertArrayEquals(arrayOf<Long>(1, 4, 3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by tags correctly`() {
        val results = search(SearchRequest(tags = listOf("new")))
        Assertions.assertEquals(3, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(1, 3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by bounty type correctly`() {
        val results = search(SearchRequest(bountyType = BountyType.Feature))
        Assertions.assertEquals(3, results.total)
        Assertions.assertArrayEquals(arrayOf<Long>(1, 3, 2), results.content.map { it.issueId }.toTypedArray())
    }

    @Test
    fun `should filter by title or body correctly`() {
        val results = search(SearchRequest(contentContains = "title-4"))
        Assertions.assertArrayEquals(arrayOf<Long>(4, 3, 2), results.content.map { it.issueId }.toTypedArray())
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