package io.prhunter.api.bounty

import io.prhunter.api.contract.ContractService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.methods.request.EthFilter
import java.math.BigInteger
import javax.annotation.PostConstruct


@Service
class BountyBlockchainService(val bountyRepository: BountyRepository, val contractService: ContractService) {

    companion object{
        const val EVERY_30_SECONDS = "0/2 * * * * *"
    }

    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun updateBountyStates(){
//        log.info { "Updating pending bounties" }
//        val allBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
//        log.info { "Found ${allBounties.size} bounties in pending state" }
        log.info { "Subscribing to bounty factory events" }
        val blockHash = "0xac040fc3db8a14d1d7eaa2b1470a07d8e39c926d3220cbba5afc1a5a91b1ae24"
        contractService.bountyFactory.bountyCreatedEventFlowable(EthFilter(blockHash)).subscribe{
            log.info("Event: ${it.bountyAddress}")
        }
    }

    // if there is an event for a new contract that has been created
    // monitor that contract
    // if it's not persisted to the chain within, let's say 6h, it becomes failed

}