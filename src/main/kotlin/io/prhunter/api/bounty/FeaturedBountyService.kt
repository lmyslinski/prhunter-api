package io.prhunter.api.bounty

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.prhunter.api.bounty.api.BountyView
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class FeaturedBountyService(
    private val bountyService: BountyService
) {

    private val FEATURED_KEY = "FEATURED"

    private val cache: Cache<String, List<BountyView>> = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build()

    fun getFeaturedBounties(): List<BountyView> {
        return cache.get(FEATURED_KEY) {
            runBlocking {
                return@runBlocking bountyService.getFeaturedBounties()
            }
        }
    }
}