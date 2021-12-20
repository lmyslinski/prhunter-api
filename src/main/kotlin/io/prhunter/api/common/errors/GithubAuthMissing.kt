package io.prhunter.api.common.errors

import org.springframework.http.HttpStatus

class GithubAuthMissing : ApiException("You need to configure your Github account.", HttpStatus.FORBIDDEN)