package io.prhunter.api.user

import com.google.firebase.auth.UserRecord
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.prhunter.api.auth.FirebaseService
import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.github.auth.GithubTokenRequest
import io.prhunter.api.github.client.GithubRestClient
import io.prhunter.api.github.client.GithubUserData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class UserAccountServiceTest(
    @Autowired val userAccountRepository: UserAccountRepository,
) {

    @MockkBean
    private val githubRestClient: GithubRestClient? = null

    @MockkBean
    private val firebaseService: FirebaseService? = null

    lateinit var userAccountService: UserAccountService

    @BeforeEach
    fun setup() {
        userAccountService = UserAccountService(userAccountRepository, githubRestClient!!, firebaseService!!)
        userAccountRepository.deleteAll()
    }

    @Test
    fun `should create new account on token update if missing`() {
        coEvery { githubRestClient!!.getGithubUserData(any()) } returns GithubUserData("login", 0L)
        val githubTokenUpdate = GithubTokenRequest("id", "access-token")
        userAccountService.updateGithubToken(githubTokenUpdate)
        val user = userAccountRepository.findByFirebaseUserId("id")
        Assertions.assertEquals(githubTokenUpdate.accessToken, user?.githubAccessToken)
    }

    @Test
    fun `should update existing account github token`() {
        coEvery { githubRestClient!!.getGithubUserData(any()) } returns GithubUserData("login", 0L)
        userAccountRepository.save(UserAccount("new-id"))
        userAccountService.updateGithubToken(GithubTokenRequest("new-id", "access-token-2"))
        val user = userAccountRepository.findByFirebaseUserId("new-id")
        Assertions.assertEquals("access-token-2", user?.githubAccessToken)
    }

    @Test
    fun `should update user email in firebase`() {
        val fbUser = FirebaseUser("id", "name", "url")
        every { firebaseService!!.updateUserEmail(any(), any()) } returns mockk()
        userAccountService.updateUserAccount(fbUser, UpdateUserAccount("new-email", null))
        verify(exactly = 1) { firebaseService!!.updateUserEmail("id", "new-email") }
    }

    @Test
    fun `should update user eth wallet and create a user if doesn't exist`(){
        val fbUser = FirebaseUser("another-id", "", "")
        userAccountService.updateUserAccount(fbUser, UpdateUserAccount(null, "0x0"))
        val userOpt = userAccountRepository.findById(fbUser.id)
        Assertions.assertEquals("0x0", userOpt.get().ethWalletAddress)
    }

    @Test
    fun `should update existing user eth wallet`(){
        userAccountRepository.save(UserAccount("id-3"))
        val fbUser = FirebaseUser("id-3", "", "")
        userAccountService.updateUserAccount(fbUser, UpdateUserAccount(null, "0x0x"))
        val userOpt = userAccountRepository.findById(fbUser.id)
        Assertions.assertEquals("0x0x", userOpt.get().ethWalletAddress)
    }

}