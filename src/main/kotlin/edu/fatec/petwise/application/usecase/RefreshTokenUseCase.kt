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
        logger.info("Renovando token...")

        if (!jwtService.validateToken(refreshToken, "REFRESH")) {
            throw IllegalArgumentException("Refresh token inválido ou expirado")
        }

        val email = jwtService.extractEmail(refreshToken)
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Usuário não encontrado")

        val newAccessToken = jwtService.generateToken(
            userId = user.id.toString(),
            email = user.email.value,
            userType = user.userType
        )

        val newRefreshToken = jwtService.generateRefreshToken(
            userId = user.id.toString(),
            email = user.email.value
        )

        logger.info("Novo token gerado com sucesso para $email")

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
}
