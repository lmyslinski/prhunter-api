package io.prhunter.api.github

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GithubSecrets(
    @Value("\${github.appId}") val appId: String,
    @Value("\${github.clientId}") val clientId: String,
    @Value("\${github.privateKey}") val privateKey: String,
    @Value("\${github.successUrl}") val successUrl: String,
    @Value("\${github.webhookSecret}") val webhookSecret: String
)