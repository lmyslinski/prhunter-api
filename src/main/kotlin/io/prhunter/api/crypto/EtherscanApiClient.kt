package io.prhunter.api.crypto

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.util.Value
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class EtherscanApiClient(
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
    @Value("\${etherScan.apiKey}") private val etherScanApiKey: String,
    @Value("\${etherScan.network}") private val etherScanNetwork: String
) {

    private val etherScanBaseUrl = "https://${getNetworkPrefix()}.etherscan.io/api"
    private fun getNetworkPrefix(): String {
        return if(etherScanNetwork == "mainnet"){
            "api"
        }else{
            return "api-${etherScanNetwork}"
        }
    }


    private val fallbackValue = BigDecimal.valueOf(1L)

    suspend fun getTransaction(txHash: String): String {
        return try {
            val response = httpClient.get<HttpResponse>(etherScanBaseUrl) {
                parameter("module", "transaction")
                parameter("action", "getstatus")
                parameter("txhash", txHash)
                parameter("apikey", etherScanApiKey)
            }
            return response.readText()
        } catch (ex: Throwable) {
            log.error(ex) { "Could not get $ticker price from coinGecko" }
            fallbackValue
        }
    }
}