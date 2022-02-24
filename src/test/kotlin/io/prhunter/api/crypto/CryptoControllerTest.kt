package io.prhunter.api.crypto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.math.BigDecimal


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CryptoControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
) {

    @MockkBean
    private val coinGeckoApiService: CoinGeckoApiService? = null

    @Test
    fun `should return current crypto pricing`() {

        every { coinGeckoApiService!!.getCurrentPrice(CryptoCurrency.ETH) }.returns(BigDecimal.valueOf(4312.22))
        every { coinGeckoApiService!!.getCurrentPrice(CryptoCurrency.BNB) }.returns(BigDecimal.valueOf(3.232))

        val response = mockMvc.get("/crypto/prices") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual: List<CryptoPrice> = objectMapper.readValue(response)
        Assertions.assertEquals(
            listOf(
                CryptoPrice(CryptoCurrency.ETH.name, BigDecimal.valueOf(4312.22)),
                CryptoPrice(CryptoCurrency.BNB.name, BigDecimal.valueOf(3.232))
            ), actual
        )
    }
}