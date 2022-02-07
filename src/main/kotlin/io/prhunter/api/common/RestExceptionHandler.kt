package io.prhunter.api.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.prhunter.api.common.errors.ApiException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.function.Consumer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionHandler(private val objectMapper: ObjectMapper) : ResponseEntityExceptionHandler(),
    AuthenticationFailureHandler {

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

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = apiError(ex, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(RuntimeException::class)
    protected fun handleGenericError(
        ex: RuntimeException
    ): ResponseEntity<Any> = apiError(ex, HttpStatus.INTERNAL_SERVER_ERROR)

    @ExceptionHandler(ApiException::class)
    protected fun handleApiErrors(
        ex: ApiException
    ): ResponseEntity<Any> = apiError(ex, ex.status)

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val errorResponse = apiError(exception, HttpStatus.UNAUTHORIZED)
        response.outputStream.println(objectMapper.writeValueAsString(errorResponse))
    }


}