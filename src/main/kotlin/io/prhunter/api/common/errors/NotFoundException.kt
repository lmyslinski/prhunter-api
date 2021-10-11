package io.prhunter.api.common.errors

import java.lang.RuntimeException

class RepoAdminAccessRequired : RuntimeException("The user does not have admin access to the requested repository")