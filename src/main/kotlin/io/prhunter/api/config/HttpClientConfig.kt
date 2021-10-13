package io.prhunter.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
//import org.jooq.DSLContext

@Configuration
class HttpClientConfig(
    @Autowired val objectMapper: ObjectMapper
) {

    @Bean
    fun httpClient(): HttpClient {
        return HttpClient {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.INFO
            }
            install(JsonFeature){
                serializer = JacksonSerializer(objectMapper)
            }
        }
    }
}