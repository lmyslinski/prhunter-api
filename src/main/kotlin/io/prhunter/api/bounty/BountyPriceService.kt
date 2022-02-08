package io.prhunter.api.bounty

import io.prhunter.api.crypto.CoinGeckoApiService
import io.prhunter.api.crypto.CryptoCurrency
import mu.KotlinLogging
import org.springframework.stereotype.Service


@Service
class BountyPriceService(private val bountyRepository: BountyRepository,private  val coinGeckoApiService: CoinGeckoApiService) {

    private val log = KotlinLogging.logger {}

    fun updateCurrentBountyUsdPrices(){
        log.debug { "Updating bounty USD values" }
        val bounties = bountyRepository.findAll()
        val ethPrice = coinGeckoApiService.getCurrentPrice(CryptoCurrency.ETH)
        val bnbPrice = coinGeckoApiService.getCurrentPrice(CryptoCurrency.BNB)
        bounties.forEach{
            when(CryptoCurrency.valueOf(it.bountyCurrency)){
                CryptoCurrency.ETH -> {
                    it.bountyValueUsd = it.bountyValue*ethPrice
                    bountyRepository.save(it)
                }
                CryptoCurrency.BNB -> {
                    it.bountyValueUsd = it.bountyValue*bnbPrice
                    bountyRepository.save(it)
                }
            }
        }
    }
}