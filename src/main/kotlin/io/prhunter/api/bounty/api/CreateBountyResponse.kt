package io.prhunter.api.bounty.api

import java.util.UUID

data class CreateBountyResponse(
    val newBountyId: UUID,
    val bountyFactoryAddress: String
)