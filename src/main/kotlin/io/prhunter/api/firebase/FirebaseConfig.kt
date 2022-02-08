package io.prhunter.api.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.*
import javax.annotation.PostConstruct

@Configuration
@Profile("!test")
class FirebaseConfig(
    @Value("\${firebase.privateKey}") private val privateKey: String,
) {

    @Bean
    fun firebaseApp(): FirebaseApp{
        if(privateKey.isEmpty()){
            throw RuntimeException("Configuration was set properly")
        }
        val pkDecoded = Base64.getDecoder().decode(privateKey).inputStream()
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(pkDecoded))
            .build()
        return FirebaseApp.initializeApp(options)
    }
}