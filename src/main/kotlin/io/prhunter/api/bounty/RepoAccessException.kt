package io.prhunter.api.bounty

import java.lang.RuntimeException

class NoRepoAdminAccessException : RuntimeException("The user does not have admin access to the requested repository")