package io.prhunter.api.common.errors

import org.springframework.http.HttpStatus

class NotFoundException(id: Any) : ApiException("The object with ID ${id} was not found", HttpStatus.NOT_FOUND)
