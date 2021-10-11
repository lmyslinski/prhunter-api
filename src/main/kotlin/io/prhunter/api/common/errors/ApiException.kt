package io.prhunter.api.common.errors

import org.springframework.http.HttpStatus

open class ApiException(message: String, val status: HttpStatus): RuntimeException(message)