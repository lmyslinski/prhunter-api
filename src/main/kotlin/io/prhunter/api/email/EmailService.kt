package io.prhunter.api.email

import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class EmailService(
    @Autowired val emailClient: EmailClient
) {

    fun sendContactEmail(contactMessageDto: ContactMessageDto) {
        emailClient.sendContactEmail(contactMessageDto)
    }

    fun sendRegistrationEmail(email: String) {
        val link = FirebaseAuth.getInstance().generateEmailVerificationLink(email)
        println(link)
        emailClient.sendRegistrationEmail(email, link)
    }

    fun sendEmailVerification(email: String) {
        val link = FirebaseAuth.getInstance().generateEmailVerificationLink(email)
        emailClient.sendEmailVerificationEmail(email, link)
    }
}