package io.prhunter.api.github

interface GithubAuthService {
    fun getInstallationAuthToken(installationId: Long): String
}