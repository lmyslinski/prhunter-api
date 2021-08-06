package io.prhunter.api.installation

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Installation(
    @Id
    var id: Long,
    var accountId: Long,
    var accountType: String,
    var senderId: Long,
    var senderType: String,
    var createdAt: Instant = Instant.now()
)