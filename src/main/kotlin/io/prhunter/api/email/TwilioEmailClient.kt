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
        const val FROM: String = "no-reply@prhunter.io"
        const val CONTACT_FORM_TEMPLATE_ID = "d-cf4326cc29f74f708ba0b11536520638"
        const val SIGNUP_TEMPLATE_ID = "d-46638797fd2740cdaf3ddf9966b0a473"
        const val EMAIL_VERIFICATION_TEMPLATE_ID = "d-a12a5100c8634aa7a2e02fde695ffa20"
    }

    override fun sendRegistrationEmail(email: String, link: String){
        val mail = Mail()
        mail.setFrom(Email(FROM, "PRHunter Team"))
        mail.setReplyTo(Email(SUPPORT, "PRHunter Team"))
        mail.setTemplateId(SIGNUP_TEMPLATE_ID)
        val personalization = Personalization()
        personalization.addDynamicTemplateData("link", link)
        personalization.addTo(Email(email))
        mail.addPersonalization(personalization)
        sendInternal(mail)
    }

    override fun sendEmailVerificationEmail(email: String, link: String) {
        val mail = Mail()
        mail.setFrom(Email(FROM, "PRHunter Team"))
        mail.setReplyTo(Email(SUPPORT, "PRHunter Team"))
        mail.setTemplateId(EMAIL_VERIFICATION_TEMPLATE_ID)
        val personalization = Personalization()
        personalization.addDynamicTemplateData("link", link)
        personalization.addTo(Email(email))
        mail.addPersonalization(personalization)
        sendInternal(mail)
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
