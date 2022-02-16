package io.prhunter.api

import com.github.kagkarlsson.scheduler.Scheduler
import com.ninjasquad.springmockk.MockkBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service


@Configuration
class TestConfiguration {

    @MockkBean
    private val scheduler: Scheduler? = null
}