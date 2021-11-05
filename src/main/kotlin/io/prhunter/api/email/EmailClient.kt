package io.prhunter.api.email

interface EmailClient {
    fun sendContactEmail(contactMessageDto: ContactMessageDto)
}