package io.prhunter.api.github

import io.prhunter.api.github.serverauth.GithubAuthService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class GithubAuthServiceMock : GithubAuthService {
    override fun getInstallationAuthToken(installationId: Long): String {
        return ""
    }
}