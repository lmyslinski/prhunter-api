package io.prhunter.api.bounty

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.web3j.protocol.Web3j
import org.web3j.protocol.admin.Admin
import org.web3j.protocol.http.HttpService


@Service
class BountyBlockchainService(val bountyRepository: BountyRepository) {

    companion object{
        const val EVERY_30_SECONDS = "0/10 * * * * *"
    }

    private val log = KotlinLogging.logger {}
    var web3j = Web3j.build(HttpService("<your_node_url>"))




    @Scheduled(cron = EVERY_30_SECONDS)
    fun updateBountyStates(){
        log.info { "Updating pending bounties" }
        val allBounties = bountyRepository.findAllByBountyStatus(BountyStatus.PENDING)
        log.info { "Found ${allBounties.size} bounties in pending state" }
        if(allBounties.isNotEmpty()){
            val first = allBounties.first()
            val receipt = web3j.ethGetTransactionByHash(first.transactionHash).send()
            if(receipt == null){
                log.info { "Transaction is not completed yet" }
            }else{
                log.info { "Transaction completed: ${receipt.rawResponse}"}
            }
        }
    }
}