package io.prhunter.api.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ContactMessageDto(val senderEmailAddress: String, val name: String, val subject: String, val message: String)

data class RegistrationEmailDto(val email: String)

@RestController
@RequestMapping("/email")
class EmailController(
        @Autowired private val emailService: EmailService
) {

    @PostMapping("/signup")
    fun sendRegistrationEmail(@RequestBody registrationEmailDto: RegistrationEmailDto){

    }

    @PostMapping("/contact")
    fun sendContactMessage(@RequestBody contactMessageDto: ContactMessageDto){
        emailService.sendContactEmail(contactMessageDto)
    }
}