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

        // 🔒 Ignora rotas públicas e do Swagger/OpenAPI
        if (path.startsWith("/api/auth/") ||
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

            // ✅ CORREÇÃO 2: validateToken agora aceita 1 parâmetro (usa "ACCESS" como padrão)
            if (jwtService.validateToken(token)) {
                // ✅ CORREÇÃO 1: Métodos renomeados para getUserIdFromToken e getRoleFromToken
                val userId = jwtService.getUserIdFromToken(token)
                val role = jwtService.getRoleFromToken(token)

                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
                val authentication =
                    UsernamePasswordAuthenticationToken(userId, null, authorities)

                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            // 🔒 SEGURANÇA: Não expõe detalhes do erro (apenas loga)
            logger.error("Erro ao processar token JWT: ${e.javaClass.simpleName}")
            // 🔒 Log detalhado apenas em nível DEBUG
            logger.debug("Detalhes do erro JWT: ${e.message}", e)
        }

        filterChain.doFilter(request, response)
    }
}
