package io.prhunter.api.user

import io.prhunter.api.auth.FirebaseService
import io.prhunter.api.auth.FirebaseUser
import io.prhunter.api.common.errors.NotFoundException
import io.prhunter.api.github.auth.GithubTokenRequest
import io.prhunter.api.github.client.GithubRestClient
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val userAccountRepository: UserAccountRepository,
    private val githubRestClient: GithubRestClient,
    private val firebaseService: FirebaseService
) {

    fun getUserAccount(firebaseUserId: String): UserAccount {
        return userAccountRepository.findByFirebaseUserId(firebaseUserId) ?: throw NotFoundException(firebaseUserId)
    }

    fun updateGithubToken(githubTokenRequest: GithubTokenRequest) {
        val ghUserData = runBlocking {
            githubRestClient.getGithubUserData(githubTokenRequest.accessToken)
        }
        val userAccount = userAccountRepository.findByFirebaseUserId(githubTokenRequest.firebaseUserId)
        if (userAccount != null) {
            userAccount.githubAccessToken = githubTokenRequest.accessToken
            userAccountRepository.save(userAccount)
        } else {
            val ghToken = UserAccount(githubTokenRequest.firebaseUserId, ghUserData.id, githubTokenRequest.accessToken)
            userAccountRepository.save(ghToken)
        }
    }

    fun getUserAccountView(firebaseUser: FirebaseUser): UserAccountView? {
        val firebaseUserRecord = firebaseService.getUserById(firebaseUser.id)
        val userAccount = userAccountRepository.findByFirebaseUserId(firebaseUser.id)
        return UserAccountView(
            firebaseUserRecord?.email,
            firebaseUserRecord?.displayName,
            userAccount?.ethWalletAddress
        )
    }

    fun updateUserAccount(firebaseUser: FirebaseUser, updateUserAccount: UpdateUserAccount) {
        if (!updateUserAccount.email.isNullOrEmpty()) {
            firebaseService.updateUserEmail(firebaseUser.id, updateUserAccount.email)
        }

        val userAccountOpt = userAccountRepository.findByFirebaseUserId(firebaseUser.id)
        if (userAccountOpt != null && updateUserAccount.ethWalletAddress != null) {
            userAccountOpt.ethWalletAddress = updateUserAccount.ethWalletAddress
            userAccountRepository.save(userAccountOpt)
        } else if (userAccountOpt == null && updateUserAccount.ethWalletAddress != null) {
            val userAccount = UserAccount(firebaseUser.id, ethWalletAddress = updateUserAccount.ethWalletAddress)
            userAccountRepository.save(userAccount)
        }
    }
}