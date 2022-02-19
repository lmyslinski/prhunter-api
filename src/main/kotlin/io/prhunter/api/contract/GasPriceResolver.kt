package io.prhunter.api.contract

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.prhunter.api.crypto.CryptoCurrency
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit

@Service
class GasPriceResolver(
    @Value("\${crypto.alchemyUrl}") private val alchemyUrl: String,
    private val ethGasStationApiClient: EthGasStationApiClient
) {
    // 100 gwei
    private val bigWeiAmount = BigDecimal.valueOf(100L).toWei()
    // 21k for mainNet
    private val mainNetGasLimit = BigInteger.valueOf(21000)
    // 4.7k for ropsten
    private val ropstenGasLimit = BigInteger.valueOf(4700)

    private val log = KotlinLogging.logger {}
    private val cache: Cache<CryptoCurrency, GasPriceInfo> = Caffeine.newBuilder()
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build()


    fun getGasProviderForTx(): StaticGasProvider {
        val gasPrice = getGasPrice()
        val gasLimit = getGasLimit()
        log.info { "Using gas price of $gasPrice and gas limit of $gasLimit" }
        return StaticGasProvider(gasPrice.toBigInteger(), gasLimit)
    }

    fun getGasPrice(): BigDecimal {
        return if(getNetwork() == EthNetwork.MAINNET){
            getMainNetGasPrice()
        }else{
            bigWeiAmount
        }
    }

    fun getGasLimit(): BigInteger{
        return if(getNetwork() == EthNetwork.MAINNET){
            mainNetGasLimit
        }else{
            ropstenGasLimit
        }
    }

    private fun getMainNetGasPrice(): BigDecimal {
        val priceInfo = getFromCacheOrFetch()
        return if(priceInfo.safeLowWait >= 30.0){
            val price = convertToWeiFromPriceInfo(priceInfo.average)
            log.info { "Using average price of $${price}. Est wait time: ${priceInfo.avgWait}m" }
            price
        }else {
            val price = convertToWeiFromPriceInfo(priceInfo.safeLow)
            log.info { "Using safeLow price of $${price}. Est wait time: ${priceInfo.safeLowWait}m" }
            price
        }
    }

    private fun getFromCacheOrFetch(): GasPriceInfo {
        return cache.get(CryptoCurrency.ETH) {
            runBlocking {
                ethGasStationApiClient.getCurrentGasPrice()
            }
        }
    }

    private fun getNetwork(): EthNetwork {
        return EthNetwork.MAINNET
//        if(alchemyUrl.contains("ropsten")){
//            return EthNetwork.ROPSTEN
//        }else{
//            return EthNetwork.MAINNET
//        }
    }

    private fun convertToWeiFromPriceInfo(longPrice: Long): BigDecimal {
        return BigDecimal.valueOf(longPrice).divide(BigDecimal.valueOf(10L)).toWei()
    }

    // converts gwei to wei
    private final fun BigDecimal.toWei(): BigDecimal{
        return this.times(BigDecimal.valueOf(1000000000))
    }
}