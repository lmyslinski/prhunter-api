package io.prhunter.api.common.errors

import org.springframework.http.HttpStatus

class EmptyInputException : ApiException("Nothing to update", HttpStatus.BAD_REQUEST)