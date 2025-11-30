package edu.fatec.petwise.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        
        if (path == "/api/auth/register" ||
            path == "/api/auth/login" ||
            path == "/api/auth/forgot-password" ||
            path == "/api/auth/reset-password" ||
            path == "/api/auth/refresh-token" ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/swagger-ui.html") ||
            path.startsWith("/actuator/health")
        ) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = authHeader.substring(7)
            val maskedToken = token.take(10) + "..." + token.takeLast(10)

            
            if (jwtService.validateToken(token)) {
                
                val userId = jwtService.getUserIdFromToken(token)
                val role = jwtService.getRoleFromToken(token)

                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
                val authentication =
                    UsernamePasswordAuthenticationToken(userId, null, authorities)

                SecurityContextHolder.getContext().authentication = authentication
                logger.debug("Authentication set successfully for user $userId with role ROLE_$role on path $path")
            } else {
                logger.warn("Token validation failed for path $path - token: $maskedToken")
            }
        } catch (e: Exception) {
            
            logger.error("Erro ao processar token JWT: ${e.javaClass.simpleName}")
            
            logger.debug("Detalhes do erro JWT: ${e.message}", e)
        }

        filterChain.doFilter(request, response)
    }
}
