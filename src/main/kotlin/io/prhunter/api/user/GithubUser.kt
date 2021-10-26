package io.prhunter.api.user

import io.prhunter.api.user.api.GithubUserView
import org.hibernate.Hibernate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import java.io.Serializable
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import kotlin.math.log

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
): Serializable, OAuth2User, UserDetails {

    override fun getName(): String = fullName ?: ""

    override fun getAttributes(): MutableMap<String, Any> = mutableMapOf()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))

    override fun getPassword(): String = ""

    override fun getUsername(): String = login

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as GithubUser

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , login = $login , email = $email , fullName = $fullName , accessToken = $accessToken , githubRegisteredAt = $githubRegisteredAt , registeredAt = $registeredAt )"
    }
}

fun GithubUser.toView() = GithubUserView(this.id, this.login, this.email, this.fullName)