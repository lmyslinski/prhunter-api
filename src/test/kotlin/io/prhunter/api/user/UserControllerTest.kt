package io.prhunter.api.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.user.api.GithubUserView
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    private val testUser = User(23L, "test-user", null, "Johny Cash", "tmp-token", Instant.now(), Instant.now())

    @Test
    fun `should return github user view if signed in`() {
        val response = mockMvc.get("/user") {
            accept = MediaType.APPLICATION_JSON
            with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(testUser))
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<GithubUserView>(response)
        Assertions.assertEquals(testUser.toView(), actual)
    }

    @Test
    fun `should return 401 if not signed in`(){
        mockMvc.get("/user") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status {
                isEqualTo(HttpStatus.UNAUTHORIZED.value())
            }
        }
    }
}