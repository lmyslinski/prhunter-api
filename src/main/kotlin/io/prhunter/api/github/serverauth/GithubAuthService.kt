package io.prhunter.api.github.serverauth

interface GithubAuthService {
    fun getInstallationAuthToken(installationId: Long): String
}