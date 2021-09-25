package io.prhunter.api.bounty

import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BountyService(val bountyRepository: BountyRepository) {

    fun createBounty(createBountyRequest: CreateBountyRequest): Bounty {
        val bounty = Bounty(
            repoId = createBountyRequest.repoId,
            issueId = createBountyRequest.issueId,
            title = createBountyRequest.title,
            body = createBountyRequest.body,
            bountyValue = createBountyRequest.bountyValue,
            bountyCurrency = createBountyRequest.bountyCurrency,
            languages = createBountyRequest.languages.toTypedArray()
        )
        return bountyRepository.save(bounty)
    }

    fun getBounty(id: Long): Bounty {
        return bountyRepository.findById(id).orElseThrow()
    }

    fun list(): List<Bounty> {
        return bountyRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"))
    }

    fun updateBounty(id: Long, updateBountyRequest: UpdateBountyRequest): Bounty {
        val bounty = getBounty(id)
        val updatedBounty = bounty.copy(
            body = updateBountyRequest.body,
            title = updateBountyRequest.title,
            languages = updateBountyRequest.languages.toTypedArray(),
            bountyCurrency = updateBountyRequest.bountryCurrency,
            bountyValue = updateBountyRequest.bountyValue,
            updatedAt = Instant.now(),
        )
        return bountyRepository.save(updatedBounty)
    }

}
