package io.prhunter.api.user

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class UserAccount(
    @Id
    var firebaseUserId: String,
    var githubUserId: Long? = null,
    var githubAccessToken: String?= null,
    var ethWalletAddress: String? = null
)