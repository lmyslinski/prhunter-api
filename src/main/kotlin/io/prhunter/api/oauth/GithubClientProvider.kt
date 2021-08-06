package io.prhunter.api.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(GithubJwtInterceptor(githubSecrets))
            .addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://github.com")
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(okHttpClient).build()

        return retrofit.create(GithubClient::class.java)
    }
}

