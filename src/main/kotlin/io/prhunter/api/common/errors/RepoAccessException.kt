package io.prhunter.api.bounty

import io.prhunter.api.common.errors.ApiException
import org.springframework.http.HttpStatus
import java.util.*

class NotFoundException(id: Any) : ApiException("The object with ID ${id} was not found", HttpStatus.NOT_FOUND)