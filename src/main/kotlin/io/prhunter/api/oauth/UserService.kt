package io.prhunter.api.oauth

import io.prhunter.api.user.GithubUserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class UserService(private val githubUserRepository: GithubUserRepository) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    val default = DefaultOAuth2UserService()

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
       return default.loadUser(userRequest)
//        githubUserRepository.findById()
//        return  loadUser(userRequest)
//        return super.loadUser(userRequest)
    }


}