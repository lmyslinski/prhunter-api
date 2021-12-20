package io.prhunter.api.common.errors

import org.springframework.http.HttpStatus

class BountyAlreadyExists : ApiException("This issue already has a bounty. You cannot have multiple bounties for the same issue.", HttpStatus.BAD_REQUEST)