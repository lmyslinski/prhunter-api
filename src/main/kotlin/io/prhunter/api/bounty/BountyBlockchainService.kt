package io.prhunter.api.bounty

import io.prhunter.api.contract.ContractService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.web3j.protocol.core.methods.request.EthFilter


@Service
class BountyBlockchainService(val bountyRepository: BountyRepository, val contractService: ContractService) {

    companion object{
        const val EVERY_30_SECONDS = "0/2 * * * * *"
    }

    private val log = KotlinLogging.logger {}

//    @Scheduled(cron = EVERY_30_SECONDS)
    fun updateBountyStates(){
        log.info { "Updating pending bounties" }
        val allBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
        log.info { "Found ${allBounties.size} bounties in pending state" }
        if(allBounties.isNotEmpty()){
            val first = allBounties.first()
            var qq = contractService.bountyFactory.bountyCreatedEventFlowable(EthFilter()).blockingFirst()
            log.info { "First: $qq" }
        }
    }

    // if there is an event for a new contract that has been created
    // monitor that contract
    // if it's not persisted to the chain within, let's say 6h, it becomes failed

}