package io.prhunter.api.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.prhunter.api.TestDataProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest(
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val mockMvc: MockMvc
) {

    @MockkBean
    private val userAccountService: UserAccountService? = null

    @Test
    fun `should return user account view if signed in`() {
        TestDataProvider.setAuthenticatedContext()
        val userAccountView = UserAccountView("test-email", false, "display", "0x012312")
        every { userAccountService?.getUserAccountView(any()) }.returns(userAccountView)
        val response = mockMvc.get("/user") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn().response.contentAsString

        val user = objectMapper.readValue<UserAccountView>(response)
        Assertions.assertEquals(userAccountView, user)
    }

    @Test
    fun `should update the user data`(){
        TestDataProvider.setAuthenticatedContext()
        val validUpdateRequest = UpdateUserAccount("test@email.com", "0x3213ecf292af95F711ADc744Cf0F4fdc5c0B8E73")
        every { userAccountService?.updateUserAccount(any(), any()) } returns Unit
        mockMvc.put("/user") {
            content = objectMapper.writeValueAsString(validUpdateRequest)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }
    }

    @Test
    fun `should reject invalid update email request`(){
        TestDataProvider.setAuthenticatedContext()
        mockMvc.put("/user") {
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(UpdateUserAccount("invalid-email", null))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is4xxClientError() }
        }
    }

    @Test
    fun `should reject invalid update eth wallet request`(){
        TestDataProvider.setAuthenticatedContext()

        mockMvc.put("/user") {
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(UpdateUserAccount(null, "invalid-wallet"))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is4xxClientError() }
        }
    }

    @Test
    fun `should reject empty update request`(){
        TestDataProvider.setAuthenticatedContext()

        mockMvc.put("/user") {
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(UpdateUserAccount(null, null))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is4xxClientError() }
        }
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