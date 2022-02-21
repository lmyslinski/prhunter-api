package io.prhunter.api.github

import io.prhunter.api.bounty.Bounty
import io.prhunter.api.contract.ContractService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import javax.transaction.NotSupportedException

@Service
@Profile("test")
class MockEthContractService(
) : ContractService {
    override fun checkPendingContracts() {
        throw NotSupportedException()
    }

    override fun payoutBounty(targetAddress: String, bounty: Bounty) {
        throw NotSupportedException()
    }

    override fun cleanupExpiredBounties() {
        throw NotSupportedException()
    }
}