package io.prhunter.api.crypto

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CoinGeckoApiClient(
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
) {

    private val log = KotlinLogging.logger {}
    private val coinGeckoBaseUrl = "https://api.coingecko.com/api/v3"
    private val fallbackValue = BigDecimal.valueOf(1L)

    suspend fun fetchPrice(ticker: String): BigDecimal {
        return try {
            val response = httpClient.get<HttpResponse>("$coinGeckoBaseUrl/simple/price") {
                parameter("ids", ticker)
                parameter("vs_currencies", "usd")
            }
            objectMapper.readTree(response.readText()).get(ticker).get("usd").decimalValue()
        } catch (ex: Throwable) {
            log.error(ex) { "Could not get $ticker price from coinGecko" }
            fallbackValue
        }
    }
}