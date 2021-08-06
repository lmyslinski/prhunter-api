package io.prhunter.api.installation

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class InstallationService(
    @Autowired private val installationRepository: InstallationRepository
) {

    fun registerInstallation(installation: Installation){
        installationRepository.save(installation)
        log.info { "A new app installation was created" }
    }

    fun removeInstallation(installationId: Long){
        try{
            installationRepository.deleteById(installationId)
            log.info { "An installation with id ${installationId} was deleted" }
        }catch (ex: EmptyResultDataAccessException){
            log.warn { "An installation deletion was attempted but $installationId was not found" }
        }
    }
}