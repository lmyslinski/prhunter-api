package io.prhunter.api.contract

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EthGasStationApiClient(
    private val httpClient: HttpClient,
    private val objectMapper: ObjectMapper,
    @Value("\${crypto.ethGasStationApiKey}") private val ethGasStationApiKey: String
) {

    private val ethGasStationUrl = "https://ethgasstation.info/api/ethgasAPI.json"

    suspend fun getCurrentGasPrice(): GasPriceInfo {
        try {
            val response = httpClient.get<HttpResponse>(ethGasStationUrl) {
                parameter("api-key", ethGasStationApiKey)
            }
            return objectMapper.readValue(response.readText())
        } catch (ex: Throwable) {
            throw RuntimeException("Could not get current gas price from ethGasStation", ex)
        }
    }
}