package io.prhunter.api.contract.gas

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.prhunter.api.contract.BlockchainInfo
import io.prhunter.api.crypto.CryptoCurrency
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

@Service
class GasPriceResolver(
    private val ethGasStationApiClient: OwlracleApiClient
) {
    // 100 gwei
    private val bigWeiAmount = BigDecimal.valueOf(100L).toWei()
    private val log = KotlinLogging.logger {}
    private val cache: Cache<CryptoCurrency, GasPriceInfo> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build()

    fun getGasPrice(blockchainInfo: BlockchainInfo): BigInteger {
        return if(blockchainInfo.testNet){
            log.info { "Using gas price of ${bigWeiAmount.toGwei()} gwei ${blockchainInfo.currency.ticker}" }
            bigWeiAmount.toBigInteger()
        }else{
            getMainNetGasPrice(blockchainInfo.currency).toBigInteger()
        }
    }

    fun getGasLimit(blockchainInfo: BlockchainInfo): BigInteger {
        log.info { "Using gas limit of ${blockchainInfo.gasLimit}" }
        return blockchainInfo.gasLimit
    }

    private fun getMainNetGasPrice(currency: CryptoCurrency): BigDecimal {
        val priceInfo = getFromCacheOrFetch(currency)
        val price = priceInfo.avgTx.toWei()
        log.info { "Using average price of ${price.toGwei()} gwei ${currency.ticker}. Est wait time: ${priceInfo.avgTime.setScale(0, RoundingMode.HALF_UP)}m" }
        return price
    }

    private fun getFromCacheOrFetch(currency: CryptoCurrency): GasPriceInfo {
        return cache.get(currency) {
            runBlocking {
                ethGasStationApiClient.getCurrentGasPrice(currency)
            }
        }
    }

    // converts gwei to wei
    private final fun BigDecimal.toWei(): BigDecimal{
        return this.times(BigDecimal.valueOf(1000000000))
    }

    // converts wei to gwei
    private final fun BigDecimal.toGwei(): BigDecimal{
        return this.divide(BigDecimal.valueOf(1000000000))
    }
}