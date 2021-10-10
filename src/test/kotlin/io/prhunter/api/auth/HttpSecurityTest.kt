package io.prhunter.api.auth

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HttpSecurityTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) {

    @Test
    fun `should allow requests from an allowed domain`(){
//        mockMvc.get("/bounty") {
//            accept = MediaType.APPLICATION_JSON
//
//        }.andExpect {
//            status { isEqualTo(HttpStatus.FORBIDDEN.value()) }
//        }
    }

    @Test
    fun `should reject requests from all other domains`(){

    }
}