package io.prhunter.api.crypto

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Service
class CoinGeckoApiService(
    private val coinGeckoApiClient: CoinGeckoApiClient
) {
    private val cache: Cache<CryptoCurrency, BigDecimal> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    fun getCurrentEthUsdPrice(): BigDecimal {
        return cache.get(CryptoCurrency.ETHEREUM) {
            runBlocking {
                log.info { "Fetching ${CryptoCurrency.ETHEREUM} price from coinGecko" }
                coinGeckoApiClient.fetchPrice("ethereum")
            }
        }
    }
}