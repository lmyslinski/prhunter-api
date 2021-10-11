package io.prhunter.api.bounty

import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.bounty.api.UpdateBountyRequest
import io.prhunter.api.common.errors.NotFoundException
import io.prhunter.api.github.GithubService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BountyService(val bountyRepository: BountyRepository, val githubService: GithubService) {

    fun createBounty(createBountyRequest: CreateBountyRequest, userAccessToken: String): Bounty {
        validateUserHasAccessToRepo(createBountyRequest.repoId, userAccessToken)
        val bounty = Bounty(
            repoId = createBountyRequest.repoId,
            issueId = createBountyRequest.issueId,
            title = createBountyRequest.title,
            body = createBountyRequest.body,
            bountyValue = createBountyRequest.bountyValue,
            bountyCurrency = createBountyRequest.bountyCurrency,
            languages = createBountyRequest.languages.toTypedArray(),
            tags = createBountyRequest.tags.toTypedArray(),
            experience = createBountyRequest.experience,
            bountyType = createBountyRequest.bountyType
        )
        return bountyRepository.save(bounty)
    }

    fun getBounty(id: Long): Bounty {
        return bountyRepository.findById(id).orElseThrow { NotFoundException(id) }
    }

    fun list(): List<Bounty> {
        return bountyRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"))
    }

    fun updateBounty(id: Long, updateBountyRequest: UpdateBountyRequest, userAccessToken: String): Bounty {
        val bounty = getBounty(id)
        validateUserHasAccessToRepo(bounty.repoId, userAccessToken)
        val updatedBounty = bounty.copy(
            body = updateBountyRequest.body,
            title = updateBountyRequest.title,
            languages = updateBountyRequest.languages.toTypedArray(),
            bountyCurrency = updateBountyRequest.bountryCurrency,
            bountyValue = updateBountyRequest.bountyValue,
            updatedAt = Instant.now(),
            tags = updateBountyRequest.tags.toTypedArray(),
            experience = updateBountyRequest.experience,
            bountyType = updateBountyRequest.bountyType
        )
        return bountyRepository.save(updatedBounty)
    }

    private fun validateUserHasAccessToRepo(repoId: Long, userAccessToken: String) {
        // check if the user has admin access to the repository linked in the request
        val userRepoList = githubService.listAuthenticatedUserRepos(userAccessToken)
        val hasAdminAccess = userRepoList.find { it.id == repoId }?.permissions?.admin ?: false
        if (!hasAdminAccess) {
            throw RepoAdminAccessRequired()
        }
    }

}
