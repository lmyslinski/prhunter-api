package io.prhunter.api.crypto

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class CryptoController(
    private val coinGeckoApiService: CoinGeckoApiService
) {

    @GetMapping("/crypto/prices")
    fun getCurrentCryptoPrices(): List<Pair<CryptoCurrency, BigDecimal>> {
        return CryptoCurrency.values().map { Pair(it, coinGeckoApiService.getCurrentPrice(it)) }
    }
}