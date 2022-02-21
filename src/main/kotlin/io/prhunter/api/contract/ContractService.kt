package io.prhunter.api.contract

interface ContractService{

    fun checkPendingContracts()

    fun payoutBounty(targetAddress: String, bounty: io.prhunter.api.bounty.Bounty)
    fun cleanupExpiredBounties()
}