package io.prhunter.api.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.firebase.auth.UserMetadata
import com.google.firebase.auth.UserRecord
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.prhunter.api.TestDataProvider
import io.prhunter.api.auth.AuthService
import io.prhunter.api.auth.FirebaseUser
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper
) {

    @MockkBean
    private val authService: AuthService? = null

    @Test
    fun `should return github user view if signed in`() {
        TestDataProvider.setAuthenticatedContext()
        val mockUserRecord = mockk<UserRecord>()
        every { mockUserRecord.uid }.returns(TestDataProvider.TEST_USER.id)
        every { mockUserRecord.displayName }.returns(TestDataProvider.TEST_USER.name)
        every { mockUserRecord.tenantId }.returns(TestDataProvider.TEST_USER.name)
        every { mockUserRecord.email }.returns(TestDataProvider.TEST_USER.name)
        every { mockUserRecord.phoneNumber }.returns(TestDataProvider.TEST_USER.name)
        every { mockUserRecord.isEmailVerified }.returns(true)
        every { mockUserRecord.photoUrl }.returns("")
        every { mockUserRecord.isDisabled }.returns(true)
        every { mockUserRecord.tokensValidAfterTimestamp }.returns(123L)
        every { mockUserRecord.userMetadata }.returns(UserMetadata(0L, 0L, 0L))
        every { mockUserRecord.customClaims }.returns(mapOf())
        every { mockUserRecord.providerData }.returns(arrayOf())
        every { mockUserRecord.providerId }.returns("")
        
        every { authService?.getUserById(any()) }.returns(mockUserRecord)
        mockMvc.get("/user") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn().response.contentAsString
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