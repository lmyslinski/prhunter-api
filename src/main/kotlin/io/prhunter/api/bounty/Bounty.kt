package io.prhunter.api.bounty

import com.vladmihalcea.hibernate.type.array.StringArrayType
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
    val firebaseUserId: String,
    val title: String,
    val problemStatement: String,
    val acceptanceCriteria: String,
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
    @Enumerated(EnumType.STRING)
    val bountyStatus: BountyStatus,
    val completedBy: String? = null,
    val completedAt: Instant? = null,
    val transactionHash: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

