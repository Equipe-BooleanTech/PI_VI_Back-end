package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AuthResponse
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.infrastructure.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCase(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    @Value("\${JWT_EXPIRATION}") private val jwtExpiration: Long
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(refreshToken: String): AuthResponse {
        // ✅ CORREÇÃO 6: Logs sanitizados - não expõe tokens
        logger.info("Renovando token...")

        if (!jwtService.validateToken(refreshToken, "REFRESH")) {
            logger.warn("Tentativa de refresh com token inválido ou expirado")
            throw IllegalArgumentException("Refresh token inválido ou expirado")
        }

        val email = jwtService.extractEmail(refreshToken)
        val maskedEmail = maskEmail(email)

        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Usuário não encontrado")

        val newAccessToken = jwtService.generateToken(
            userId = user.id.toString(),
            email = user.email.value,
            userType = user.userType
        )

        // ✅ MELHORIA: Rotação de refresh token (recomendação de segurança)
        val newRefreshToken = jwtService.generateRefreshToken(
            userId = user.id.toString(),
            email = user.email.value
        )

        logger.info("Novo token gerado com sucesso para usuário: ${user.id}")

        return AuthResponse(
            token = newAccessToken,
            refreshToken = newRefreshToken,
            userId = user.id.toString(),
            fullName = user.fullName,
            email = user.email.value,
            userType = user.userType.name,
            expiresIn = jwtExpiration
        )
    }

    // 🔒 SEGURANÇA: Função para mascarar email nos logs
    private fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return "***@***"

        val localPart = parts[0]
        val domain = parts[1]

        val maskedLocal = if (localPart.length <= 2) {
            "***"
        } else {
            localPart.take(2) + "***"
        }

        return "$maskedLocal@$domain"
    }
}
