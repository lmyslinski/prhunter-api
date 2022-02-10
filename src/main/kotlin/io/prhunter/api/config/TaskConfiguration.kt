package io.prhunter.api.config

import com.github.kagkarlsson.scheduler.task.helper.RecurringTask
import com.github.kagkarlsson.scheduler.task.helper.Tasks
import com.github.kagkarlsson.scheduler.task.schedule.FixedDelay
import io.prhunter.api.bounty.BountyPriceService
import io.prhunter.api.contract.ContractService
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class TaskConfiguration {

    private val log = KotlinLogging.logger {}

    @Bean
    fun updateBountyPricesTask(bountyPriceService: BountyPriceService): RecurringTask<Void>? {
        return Tasks
            .recurring("update-bounty-usd-price", FixedDelay.ofHours(1))
            .execute { _, _ ->
                log.debug { "Running scheduled bounty price task" }
                bountyPriceService.updateCurrentBountyUsdPrices()
                log.debug { "Bounty price task completed" }
            }
    }

    @Bean
    fun updatePendingContractsTask(contractService: ContractService) : RecurringTask<Void>? {
        return Tasks
            .recurring("update-pending-contracts", FixedDelay.ofSeconds(30))
            .execute { _, _ ->
                log.debug { "Running pending contracts task" }
                contractService.checkPendingContracts()
                log.debug { "Pending contracts task completed" }
            }
    }

}