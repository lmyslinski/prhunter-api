package io.prhunter.api.config

import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

class GithubRequestModifierFilter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrapperRequest = GithubRequestWrapper(request)
        filterChain.doFilter(wrapperRequest, response)
    }
}

class GithubRequestWrapper(request: HttpServletRequest): HttpServletRequestWrapper(request) {

    val newParamMap = initParameterMap()

    private fun initParameterMap(): MutableMap<String, Array<String>> {
        val originalParamMap = this.request.parameterMap
        originalParamMap.putIfAbsent("state", arrayOf("random-state"))
        return originalParamMap
    }

    override fun getParameter(name: String?): String? {
        return newParamMap.get(name!!)?.firstOrNull()
    }

    override fun getParameterValues(name: String?): Array<String> {
        return newParamMap.get(name!!) ?: arrayOf()
    }
}