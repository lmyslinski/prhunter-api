package io.prhunter.api.bounty

import com.vladmihalcea.hibernate.type.array.StringArrayType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

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
    val issueId: Long,
    val title: String,
    val body: String,
    @Type(type = "string-array")
    val languages: Array<String>,
    @Type(type = "string-array")
    val tags: Array<String>,
    val experience: Experience,
    val bountyType: BountyType,
    val bountyValue: BigDecimal,
    val bountyCurrency: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)