package io.prhunter.api.contract.gas

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.prhunter.api.crypto.CryptoCurrency
import org.springframework.stereotype.Service

@Service
class OwlracleApiClient(
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper
) {

    private fun owlracleUrl(code: String) = "https://owlracle.info/$code/gas"

    private fun getCode(currency: CryptoCurrency): String {
        return when(currency){
            CryptoCurrency.ETH -> "eth"
            CryptoCurrency.BNB -> "bsc"
        }
    }

    suspend fun getCurrentGasPrice(cryptoCurrency: CryptoCurrency): GasPriceInfo {
        try {
            val response = httpClient.get<HttpResponse>(owlracleUrl(getCode(cryptoCurrency)))
            return objectMapper.readValue(response.readText())
        } catch (ex: Throwable) {
            throw RuntimeException("Could not get current gas price from ethGasStation", ex)
        }
    }
}