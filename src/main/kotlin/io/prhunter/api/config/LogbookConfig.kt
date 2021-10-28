package io.prhunter.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.Conditions.*
import org.zalando.logbook.Logbook

@Configuration
class LogbookConfiguration {
    @Bean
    fun logbook(): Logbook {
        return Logbook.builder()
            .condition(
                exclude(
                    requestTo("/health"),
                    requestTo("/admin/**"),
                    contentType("application/octet-stream")
                )
            )
            .build()
    }
}