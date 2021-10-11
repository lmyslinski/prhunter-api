package io.prhunter.api.common

import io.prhunter.api.common.errors.ApiException
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

    private fun apiError(ex: Throwable, status: HttpStatus): ResponseEntity<Any> {
        val apiError = ApiError(status, LocalDateTime.now(), ex.message ?: "", status.value())
        logger.error(ex.message, ex)
        return ResponseEntity(apiError, apiError.status)
    }

    @ExceptionHandler(RuntimeException::class)
    protected fun handleGenericError(
        ex: RuntimeException
    ): ResponseEntity<Any> = apiError(ex, HttpStatus.INTERNAL_SERVER_ERROR)

    @ExceptionHandler(ApiException::class)
    protected fun handleApiErrors(
        ex: ApiException
    ): ResponseEntity<Any> = apiError(ex, ex.status)

}