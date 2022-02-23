package io.prhunter.api.bounty

import io.prhunter.api.TestDataProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled("Fix post launch")
class BountyServiceTest(
    @Autowired private val bountyRepository: BountyRepository,
    @Autowired private val bountyService: BountyService
) {

    @BeforeEach
    fun cleanup(){
        bountyRepository.deleteAll()
    }

    @Test
    fun `Should mark bounties as failed if not deployed with 1h`(){
        val sixtyOneMinutesAgo = Instant.now().minusSeconds(60*61L)
        val testBounty = TestDataProvider.BOUNTIES.first().copy(createdAt = sixtyOneMinutesAgo)
        bountyRepository.save(testBounty)
//        bountyService.failNonDeployedBounties()
        val afterUpdate = bountyRepository.findByIssueId(testBounty.issueId)
        assertEquals(BountyStatus.FAILED, afterUpdate?.bountyStatus)
    }

    @Test
    fun `should not fail bounties if just deployed`(){
        val fiftyNineMinutesAgo = Instant.now().minusSeconds(59)
        val testBounty = TestDataProvider.BOUNTIES.first().copy(createdAt = fiftyNineMinutesAgo)
        bountyRepository.save(testBounty)
//        bountyService.failNonDeployedBounties()
        val afterUpdate = bountyRepository.findByIssueId(testBounty.issueId)
        assertEquals(BountyStatus.PENDING, afterUpdate?.bountyStatus)
    }

}