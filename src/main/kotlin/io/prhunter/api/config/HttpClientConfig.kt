package io.prhunter.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
class HttpClientConfig(
    @Autowired val objectMapper: ObjectMapper
) {

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient(Java) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.NONE
            }
            install(JsonFeature){
                serializer = JacksonSerializer(objectMapper)
            }
            install(HttpTimeout){
                requestTimeoutMillis = 10000
            }
        }
    }
}