package io.prhunter.api.bounty

import com.vladmihalcea.hibernate.type.array.StringArrayType
import io.prhunter.api.bounty.api.BountyView
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.*

@Entity
@TypeDef(
    name = "string-array",
    typeClass = StringArrayType::class
)
data class Bounty(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val repoId: Long,
    val repoOwner: String,
    val repoName: String,
    val issueId: Long,
    val issueNumber: Long,
    val githubUserId: Long,
    val title: String,
    val body: String,
    @Type(type = "string-array")
    val languages: Array<String>,
    @Type(type = "string-array")
    val tags: Array<String>,
    @Enumerated(EnumType.STRING)
    val experience: Experience,
    @Enumerated(EnumType.STRING)
    val bountyType: BountyType,
    val bountyValue: BigDecimal,
    val bountyCurrency: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)


fun Bounty.toView(ethPrice: BigDecimal): BountyView =
    BountyView(
        this.id!!,
        this.repoId,
        this.issueId,
        this.githubUserId,
        this.title,
        this.body,
        this.languages,
        this.tags,
        this.experience,
        this.bountyType,
        this.bountyValue,
        this.bountyValue*ethPrice,
        this.bountyCurrency,
        this.createdAt,
        this.updatedAt
    )