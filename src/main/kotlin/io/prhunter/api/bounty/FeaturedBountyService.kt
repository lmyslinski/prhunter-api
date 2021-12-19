package io.prhunter.api.bounty

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.prhunter.api.bounty.api.BountyView
import io.prhunter.api.crypto.CoinGeckoApiService
import kotlinx.coroutines.runBlocking
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class FeaturedBountyService(
    private val bountyRepository: BountyRepository,
    private val coinGeckoApiService: CoinGeckoApiService
) {

    private val featuredKey = "FEATURED"
    private val pageSize = 6

    private val cache: Cache<String, List<BountyView>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    fun getFeaturedBounties(): List<BountyView> {
        return cache.get(featuredKey) {
            runBlocking {
                val newData = bountyRepository.findAll(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"))).content
                    newData.map{ it.toView(coinGeckoApiService.getCurrentEthUsdPrice())}
            }
        }
    }
}