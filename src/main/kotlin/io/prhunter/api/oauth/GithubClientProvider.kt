package io.prhunter.api.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class GithubClientProvider(
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val githubSecrets: GithubSecrets
    ) {

    @Bean
    fun githubClient(): GithubClient {
        val qq = Retrofit.Builder()
            .baseUrl("https://www.github.com")
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(OkHttpClient.Builder().addInterceptor(GithubJwtInterceptor(githubSecrets)).build()).build()
        return qq.create(GithubClient::class.java)
    }
}

