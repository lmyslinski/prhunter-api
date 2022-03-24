package io.prhunter.api.email

import io.prhunter.api.auth.FirebaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class EmailService(
    @Autowired val emailClient: EmailClient,
    private val firebaseService: FirebaseService
) {

    fun sendContactEmail(contactMessageDto: ContactMessageDto) {
        emailClient.sendContactEmail(contactMessageDto)
    }

    fun sendRegistrationEmail(email: String) {
        emailClient.sendRegistrationEmail(email, firebaseService.getEmailVerificationLink(email))
    }

    fun sendEmailVerification(email: String) {
        emailClient.sendEmailVerificationEmail(email, firebaseService.getEmailVerificationLink(email))
    }
}