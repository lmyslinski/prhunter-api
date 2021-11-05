package io.prhunter.api.email

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.io.IOException

private val log = KotlinLogging.logger {}

@Service
@Profile("!test")
class TwilioEmailClient(@Value("\${twilio.apiKey}") val apiKey: String) : EmailClient {

    private val sendGrid = SendGrid(apiKey)

    companion object {
        const val SUPPORT: String = "support@prhunter.io"
        const val CONTACT_FORM_TEMPLATE_ID = "d-cf4326cc29f74f708ba0b11536520638"

    }

    override fun sendContactEmail(contactMessageDto: ContactMessageDto) {
        val mail = Mail()
        val sender = Email(contactMessageDto.senderEmailAddress, contactMessageDto.name)
        val self = Email(SUPPORT, contactMessageDto.name)
        mail.setFrom(self)
        mail.setReplyTo(sender)
        mail.setTemplateId(CONTACT_FORM_TEMPLATE_ID)
        val personalization = Personalization()
        personalization.addDynamicTemplateData("message", contactMessageDto.message)
        personalization.addDynamicTemplateData("subject", contactMessageDto.subject)
        personalization.addDynamicTemplateData("name", contactMessageDto.name)
        personalization.addDynamicTemplateData("sender", contactMessageDto.senderEmailAddress)
        personalization.addTo(Email(SUPPORT))
        mail.addPersonalization(personalization)
        sendInternal(mail)
    }

    private fun sendInternal(mail: Mail) {
        val email = mail.personalization[0].tos[0].email ?: ""
        log.info("Sending email to {}", email)
        val request = Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()
            val response = sendGrid.api(request)
            println(response.statusCode)
            println(response.body)
            println(response.headers)
        } catch (ex: IOException) {
            throw ex
        }
    }
}
