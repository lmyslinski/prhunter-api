package io.prhunter.api.user

data class UserAccountView(
    val email: String?,
    val isEmailVerified: Boolean?,
    val displayName: String?,
    val ethWalletAddress: String?
)