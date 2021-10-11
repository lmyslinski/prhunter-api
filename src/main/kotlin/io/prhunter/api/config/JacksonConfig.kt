package io.prhunter.api.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.core.jackson.ModelResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()

        objectMapper.registerModule(JavaTimeModule())
        objectMapper.propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        return objectMapper
    }

    @Configuration
    class ObjectMapperAutoConfiguration : WebMvcConfigurer {
        override fun extendMessageConverters(converters: List<HttpMessageConverter<*>?>) {
            var objectMapper: ObjectMapper? = null
            for (converter in converters) {
                if (converter is MappingJackson2HttpMessageConverter) {
                    val jacksonConverter = converter
                    if (objectMapper == null) {
                        objectMapper = jacksonConverter.objectMapper
                    } else {
                        jacksonConverter.objectMapper = objectMapper
                    }
                }
            }
        }
    }

    @Bean
    fun modelResolver(objectMapper: ObjectMapper): ModelResolver? {
        return ModelResolver(objectMapper)
    }
}