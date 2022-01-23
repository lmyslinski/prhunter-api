package io.prhunter.api.crypto

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@Service
class CoinGeckoApiService(
    private val coinGeckoApiClient: CoinGeckoApiClient
) {
    private val cache: Cache<CryptoCurrency, BigDecimal> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    fun getCurrentPrice(currency: CryptoCurrency): BigDecimal {
        return cache.get(currency) {
            runBlocking {
                coinGeckoApiClient.fetchPrice(currency.ticker)
            }
        }
    }
}