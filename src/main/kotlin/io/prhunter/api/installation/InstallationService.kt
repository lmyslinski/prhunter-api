package io.prhunter.api.installation

import mu.KotlinLogging
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class InstallationService(
    private val installationRepository: InstallationRepository
) {

    fun registerInstallation(installation: Installation): Installation{
        val newInstallation = installationRepository.save(installation)
        log.info { "A new app installation was created" }
        return newInstallation
    }

    fun removeInstallation(installationId: Long){
        try{
            installationRepository.deleteById(installationId)
            log.info { "An installation with id ${installationId} was deleted" }
        }catch (ex: EmptyResultDataAccessException){
            log.warn { "An installation deletion was attempted but $installationId was not found" }
        }
    }

    fun getInstallationsByUserId(id: Long): List<Installation> {
        return installationRepository.findBySenderId(id)
    }
}