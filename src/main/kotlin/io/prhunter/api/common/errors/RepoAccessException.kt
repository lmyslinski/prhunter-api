package io.prhunter.api.bounty

import io.prhunter.api.common.errors.ApiException
import org.springframework.http.HttpStatus

class RepoAdminAccessRequired : ApiException("The user does not have admin access to the requested repository", HttpStatus.FORBIDDEN)