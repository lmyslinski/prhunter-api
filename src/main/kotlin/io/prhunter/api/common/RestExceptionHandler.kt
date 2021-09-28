package io.prhunter.api.common

import io.prhunter.api.bounty.RepoAdminAccessRequired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> =
        apiError(ex, status)


    @ExceptionHandler(RepoAdminAccessRequired::class)
    protected fun handleEntityNotFound(
        ex: RepoAdminAccessRequired
    ): ResponseEntity<Any> = apiError(ex, HttpStatus.FORBIDDEN)

    private fun apiError(ex: Throwable, status: HttpStatus): ResponseEntity<Any> {
        val apiError = ApiError(status, LocalDateTime.now(), ex.message ?: "", ex.localizedMessage)
        return ResponseEntity(apiError, apiError.status)
    }

}