package com.recepyigitkader.deutchebankwork.security

import com.recepyigitkader.deutchebankwork.config.FactsConfig
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AdminEndpointsFilter(private val factsConfig: FactsConfig) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if (request.requestURI.contains("/admin")) {
            val clientSecretHeader = request.getHeader("X-Client-Secret")

            if (clientSecretHeader == null || !factsConfig.secrets.contains(clientSecretHeader)) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Unauthorized: Invalid client secret")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}