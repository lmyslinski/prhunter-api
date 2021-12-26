package io.prhunter.api.common.errors

import org.springframework.http.HttpStatus

class IssueAdminAccessRequired : ApiException("The user does not have admin access to the requested issue", HttpStatus.FORBIDDEN)