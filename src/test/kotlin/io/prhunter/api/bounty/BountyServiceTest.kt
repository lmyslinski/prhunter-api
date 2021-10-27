package io.prhunter.api.bounty

import io.mockk.every
import io.mockk.mockk
import io.prhunter.api.bounty.api.CreateBountyRequest
import io.prhunter.api.github.GithubService
import io.prhunter.api.github.client.GHRepoPermissionData
import io.prhunter.api.github.client.Permissions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class BountyServiceTest {

    private val accessToken = "123"
    private val githubService = mockk<GithubService>()
    private val bountyRepository = mockk<BountyRepository>()
    private val bountyService = BountyService(bountyRepository, githubService)

    @Test
    fun `should throw if user does not have admin access to the repository`() {
        val createBountyRequest = mockk<CreateBountyRequest>()
        every { createBountyRequest.repoId }.returns(1L)
        every { githubService.listAuthenticatedUserRepos(accessToken) }.returns(
            listOf(
                GHRepoPermissionData(
                    1L,
                    "",
                    "",
                    false,
                    Permissions(false, false, false, false, false)
                )
            )
        )

        assertThrows<RepoAdminAccessRequired> { bountyService.createBounty(createBountyRequest, accessToken) }
    }

    @Test
    fun `should throw if user does not have repo access at all`() {
        val createBountyRequest = mockk<CreateBountyRequest>()
        every { createBountyRequest.repoId }.returns(1L)
        every { githubService.listAuthenticatedUserRepos(accessToken) }.returns(
            listOf(
                GHRepoPermissionData(
                    2L,
                    "",
                    "",
                    false,
                    Permissions(true, false, false, false, false)
                )
            )
        )

        assertThrows<RepoAdminAccessRequired> { bountyService.createBounty(createBountyRequest, accessToken) }
    }

    @Test
    fun `should create bounty if user has admin access to the repository`() {
        val createBountyRequest =
            CreateBountyRequest(1L, 2L, "title", "body", listOf("scala"), listOf("new", "first"),
                Experience.Beginner,
                BountyType.Feature,BigDecimal.valueOf(20L), "ETH")
        every { githubService.listAuthenticatedUserRepos(accessToken) }.returns(
            listOf(
                GHRepoPermissionData(
                    1L,
                    "",
                    "",
                    false,
                    Permissions(true, false, false, false, false)
                )
            )
        )
        val bounty = mockk<Bounty>()
        every { bountyRepository.save(any()) }.returns(bounty)
        val expected = bountyService.createBounty(createBountyRequest, accessToken)
        assertEquals(bounty, expected)
    }
}