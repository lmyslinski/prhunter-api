package io.prhunter.api.github.webhooks

import com.github.kagkarlsson.scheduler.Scheduler
import com.github.kagkarlsson.scheduler.task.TaskInstance
import io.mockk.*
import io.prhunter.api.bounty.Bounty
import io.prhunter.api.bounty.BountyService
import io.prhunter.api.bounty.BountyStatus
import org.junit.jupiter.api.Test

class IssueHandlerTest {

    val bountyService = mockk<BountyService>()
    val scheduler = mockk<Scheduler>()
    val issueHandler = IssueHandler(bountyService, scheduler)

    @Test
    fun `should schedule bounty closure when issue is closed`() {
        val bounty = mockk<Bounty>()
        every { bountyService.getBountyByIssueId(any()) }.returns(bounty)
        every { scheduler.schedule(any<TaskInstance<CloseIssueData>>(), any()) }.just(Runs)
        issueHandler.handleIssueClosed(1L)

        verify(exactly = 1) { scheduler.schedule(any<TaskInstance<CloseIssueData>>(), any()) }
    }

    @Test
    fun `should cancel a bounty if it's active`() {
        val bounty = mockk<Bounty>()
        every { bounty.bountyStatus } returns BountyStatus.ACTIVE
        every { bountyService.getBountyByIssueId(any()) }.returns(bounty)
        every { bountyService.cancelBounty(any(), any()) } just Runs
        issueHandler.closeTheIssueIfBountyNotCompleted(1L)

        verify(exactly = 1) { bountyService.cancelBounty(any(), any()) }
    }

    @Test
    fun `should cancel a bounty if it's pending`() {
        val bounty = mockk<Bounty>()
        every { bounty.bountyStatus } returns BountyStatus.PENDING
        every { bountyService.getBountyByIssueId(any()) }.returns(bounty)
        every { bountyService.cancelBounty(any(), any()) } just Runs
        issueHandler.closeTheIssueIfBountyNotCompleted(1L)

        verify(exactly = 1) { bountyService.cancelBounty(any(), any()) }
    }

    @Test
    fun `should ignore issue closed event when bounty is not active`() {
        val bounty = mockk<Bounty>()
        every { bounty.bountyStatus } returns BountyStatus.COMPLETED
        every { bountyService.getBountyByIssueId(any()) }.returns(bounty)
        issueHandler.closeTheIssueIfBountyNotCompleted(1L)

        verify(exactly = 0) { bountyService.cancelBounty(any(), any()) }
    }

    @Test
    fun `should ignore issue closed if no bounty found for issue`() {
        every { bountyService.getBountyByIssueId(any()) }.returns(null)
        issueHandler.closeTheIssueIfBountyNotCompleted(1L)

        verify(exactly = 0) { bountyService.cancelBounty(any(), any()) }
    }
}