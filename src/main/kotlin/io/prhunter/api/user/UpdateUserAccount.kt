package io.prhunter.api.user

import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

data class UpdateUserAccount(

    @field:Email
    val email: String?,

    @field:Pattern(regexp = "^0x[a-fA-F0-9]{40}\$")
    val ethWalletAddress: String?
)