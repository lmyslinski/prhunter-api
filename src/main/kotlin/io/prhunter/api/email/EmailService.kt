package io.prhunter.api.email

import io.prhunter.api.auth.FirebaseUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class EmailService(@Autowired val emailClient: EmailClient) {

    fun sendContactEmail(contactMessageDto: ContactMessageDto) {
        emailClient.sendContactEmail(contactMessageDto)
    }

    fun sendRegistrationEmail(email: String, link: String) {
        emailClient.sendRegistrationEmail(email, link)
    }
}