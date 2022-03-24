package io.prhunter.api.email

interface EmailClient {
    fun sendContactEmail(contactMessageDto: ContactMessageDto)
    fun sendRegistrationEmail(email: String, link: String)
    fun sendEmailVerificationEmail(email: String, link: String)
}