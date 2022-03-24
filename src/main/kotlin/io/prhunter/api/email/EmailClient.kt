package io.prhunter.api.email

interface EmailClient {
    fun sendContactEmail(contactMessageDto: ContactMessageDto)
    fun sendRegistrationEmail(email: String, link: String)
}