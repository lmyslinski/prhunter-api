package io.prhunter.api.email

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class MockEmailClient : EmailClient {
    override fun sendContactEmail(contactMessageDto: ContactMessageDto) {

    }

    override fun sendRegistrationEmail(email: String, link: String) {

    }
}