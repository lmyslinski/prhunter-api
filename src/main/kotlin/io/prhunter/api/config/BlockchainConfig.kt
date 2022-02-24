package io.prhunter.api.config

import io.prhunter.api.contract.BlockchainInfo
import io.prhunter.api.crypto.CryptoCurrency
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigInteger

@Configuration
class BlockchainConfig(
    @Value("\${crypto.ethBountyFactoryAddress}") val ethBountyFactoryAddress: String,
    @Value("\${crypto.ethEtherscanUrl}") val ethEtherscanUrl: String,
    @Value("\${crypto.ethRpcUrl}") val ethRpcUrl: String,
    @Value("\${crypto.ethPkey}") val ethPkey: String,
    @Value("\${crypto.ethGasLimit}") val ethGasLimit: BigInteger,
    @Value("\${crypto.bscBountyFactoryAddress}") val bscBountyFactoryAddress: String,
    @Value("\${crypto.bscEtherscanUrl}") val bscEtherscanUrl: String,
    @Value("\${crypto.bscRpcUrl}") val bscRpcUrl: String,
    @Value("\${crypto.bscGasLimit}") val bscGasLimit: BigInteger,
    @Value("\${crypto.isTestNet}") val isTestNet: Boolean
) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun bscBlockchainInfo(): BlockchainInfo {
        if(isTestNet){
            log.info { "Running on BSC Testnet - Bounty Factory URL: $bscBountyFactoryAddress, RPC: $bscRpcUrl" }
        }else{
            log.info { "Running on BSC Mainnet - Bounty Factory URL: $bscBountyFactoryAddress, RPC: $bscRpcUrl" }
        }

        return BlockchainInfo(
            CryptoCurrency.BNB,
            bscBountyFactoryAddress,
            ethPkey,
            bscEtherscanUrl,
            bscRpcUrl,
            bscGasLimit,
            isTestNet
        )
    }

    @Bean
    fun ethBlockchainInfo(): BlockchainInfo {
        if(isTestNet){
            log.info { "Running on BSC Testnet - Bounty Factory URL: $ethBountyFactoryAddress, RPC: $ethRpcUrl" }
        }else{
            log.info { "Running on BSC Mainnet - Bounty Factory URL: $ethBountyFactoryAddress, RPC: $ethRpcUrl" }
        }

        return BlockchainInfo(
            CryptoCurrency.ETH,
            ethBountyFactoryAddress,
            ethPkey,
            ethEtherscanUrl,
            ethRpcUrl,
            ethGasLimit,
            isTestNet
        )
    }
}