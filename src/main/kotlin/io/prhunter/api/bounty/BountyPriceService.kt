package io.prhunter.api.bounty

import io.prhunter.api.crypto.CoinGeckoApiService
import io.prhunter.api.crypto.CryptoCurrency
import mu.KotlinLogging
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@EnableScheduling
@Service
class BountyPriceService(val bountyRepository: BountyRepository, val coinGeckoApiService: CoinGeckoApiService) {

    companion object{
        const val EVERY_HOUR = "0 0 * * * *"
    }

    private val log = KotlinLogging.logger {}

    @Scheduled(cron = EVERY_HOUR)
    fun updateCurrentBountyUsdPrices(){
        log.info { "Updating bounty USD values" }
        val bounties = bountyRepository.findAll()
        val ethPrice = coinGeckoApiService.getCurrentPrice(CryptoCurrency.ETH)
        val bnbPrice = coinGeckoApiService.getCurrentPrice(CryptoCurrency.BNB)
        bounties.forEach{
            when(CryptoCurrency.valueOf(it.bountyCurrency)){
                CryptoCurrency.ETH -> {
                    log.info { "Value before: ${it.bountyValueUsd}" }
                    it.bountyValueUsd = it.bountyValue*ethPrice
                    log.info { "Value after: ${it.bountyValueUsd}" }
                    bountyRepository.save(it)
                }
                CryptoCurrency.BNB -> {
                    log.info { "Value before: ${it.bountyValueUsd}" }
                    it.bountyValueUsd = it.bountyValue*bnbPrice
                    log.info { "Value after: ${it.bountyValueUsd}" }
                    bountyRepository.save(it)
                }
            }
        }
    }
}