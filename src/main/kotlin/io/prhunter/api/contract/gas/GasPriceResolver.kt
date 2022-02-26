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
import java.util.concurrent.TimeUnit

@Service
class GasPriceResolver(
    private val ethGasStationApiClient: OwlracleApiClient
) {
    // 50 gwei
    private val eth100Wei = BigDecimal.valueOf(100L).toWei()
    private val bsc20Wei = BigDecimal.valueOf(20L).toWei()
    private val log = KotlinLogging.logger {}
    private val cache: Cache<CryptoCurrency, GasPriceInfo> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build()

    fun getGasPrice(blockchainInfo: BlockchainInfo): BigInteger {
        return if(blockchainInfo.testNet && blockchainInfo.currency == CryptoCurrency.ETH){
            log.info { "Using gas price of ${eth100Wei.toGwei()} gwei ${blockchainInfo.currency.name}" }
            eth100Wei.toBigInteger()
        }else if(blockchainInfo.testNet && blockchainInfo.currency == CryptoCurrency.BNB){
            log.info { "Using gas price of ${bsc20Wei.toGwei()} gwei ${blockchainInfo.currency.name}" }
            bsc20Wei.toBigInteger()
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
        log.info { "Using gas price of ${priceInfo.baseFee} gwei ${currency.name}" }
        return priceInfo.baseFee.toWei()
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