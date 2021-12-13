package io.prhunter.api.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfig(
    @Value("\${firebase.privateKey}") private val privateKey: String,
) {

    @Bean
    fun firebaseApp(): FirebaseApp{
        val pkDecoded = Base64.getDecoder().decode(privateKey).inputStream()
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(pkDecoded))
            .build()
        return FirebaseApp.initializeApp(options)
    }
}