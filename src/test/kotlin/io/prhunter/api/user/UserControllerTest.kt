package io.prhunter.api.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.prhunter.api.auth.FirebaseUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @Test
    fun `should return github user view if signed in`() {
        val response = mockMvc.get("/user") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }.andReturn().response.contentAsString

        val actual = objectMapper.readValue<FirebaseUser>(response)
//        Assertions.assertEquals(testUser.toView(), actual)
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