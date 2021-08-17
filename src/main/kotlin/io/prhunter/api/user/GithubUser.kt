package io.prhunter.api.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import java.io.Serializable
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class GithubUser(
    @Id
    var id: Long,
    var login: String,
    var email: String?,
    var fullName: String?,
    var accessToken: String,
    var githubRegisteredAt: Instant,
    var registeredAt: Instant = Instant.now()
): Serializable, OAuth2User {

    override fun getName(): String = login

    override fun getAttributes(): MutableMap<String, Any> = mutableMapOf()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
}