package io.prhunter.api.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
open class EmailService(@Autowired val emailClient: EmailClient) {

    fun sendContactEmail(contactMessageDto: ContactMessageDto) {
        emailClient.sendContactEmail(contactMessageDto)
    }
}