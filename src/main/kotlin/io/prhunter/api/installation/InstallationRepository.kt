package io.prhunter.api.installation

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InstallationRepository : JpaRepository<Installation, Long>