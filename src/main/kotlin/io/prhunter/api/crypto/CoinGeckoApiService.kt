package io.prhunter.api.crypto

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CoinGeckoApiService(
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
) {

    fun getCurrentEthUsdPrice(): BigDecimal{
        return BigDecimal.ONE
    }
}