package io.prhunter.api.oauth

import io.prhunter.api.user.GithubUser
import io.prhunter.api.user.GithubUserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(private val githubUserRepository: GithubUserRepository) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    val default = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
       val user: OAuth2User = default.loadUser(userRequest)
        val id = user.getAttribute<Long>("id")!!
        val userOpt = githubUserRepository.findById(id)
        if(userOpt.isEmpty){
            val githubUser = GithubUser(
                user.name.toLong(),
                user.getAttribute<String>("login")!!,
                user.getAttribute<String>("email"),
                user.getAttribute<String>("name"),
                userRequest.accessToken.tokenValue,
                Instant.parse(user.getAttribute<String>("created_at")!!),
                Instant.now()
            )
            githubUserRepository.save(githubUser)
            return githubUser
        }else{
            return userOpt.get()
        }
    }
}