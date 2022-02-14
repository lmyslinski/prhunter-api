package io.prhunter.api.github

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.contract.ContractService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class MockEthContractService(
) : ContractService {
    override fun checkPendingContracts() {
        TODO("Not yet implemented")
    }

    override fun payoutBounty(targetAddress: String, bounty: Bounty) {
        TODO("Not yet implemented")
    }
}