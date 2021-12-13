package io.prhunter.api.github

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GithubSecrets(
    @Value("\${github.appId}") val appId: String,
    @Value("\${github.privateKey}") val privateKey: String,
    @Value("\${github.webhookSecret}") val webhookSecret: String,
)