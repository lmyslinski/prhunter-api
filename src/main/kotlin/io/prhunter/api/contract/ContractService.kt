package io.prhunter.api.contract

interface ContractService{

    fun periodicBountyUpdate()
    fun payoutBounty(targetAddress: String, bounty: io.prhunter.api.bounty.Bounty)
    fun getBountyFactoryAddress(): String
}