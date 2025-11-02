package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ForgotPasswordDto
import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.domain.entity.PasswordResetToken
import edu.fatec.petwise.domain.repository.PasswordResetTokenRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.infrastructure.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ForgotPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val jwtService: JwtService,
    @Value("\${JWT_RESET_EXPIRATION:900000}") private val resetTokenExpiration: Long // 15 minutos
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: ForgotPasswordDto): MessageResponse {
        val maskedEmail = maskEmail(request.email)
        logger.info("Solicitação de reset de senha para: $maskedEmail")

        val user = userRepository.findByEmail(request.email)

        if (user != null && user.active) {
            val resetToken = jwtService.generateResetToken(
                userId = user.id.toString(),
                email = user.email.value
            )

            val expiresAt = LocalDateTime.now().plusSeconds(resetTokenExpiration / 1000)
            
            val passwordResetToken = PasswordResetToken(
                userId = user.id!!,
                token = resetToken,
                expiresAt = expiresAt,
                used = false
            )

            passwordResetTokenRepository.save(passwordResetToken)

            logger.info("✉️ [SIMULAÇÃO] Email de reset enviado para: $maskedEmail")
            logger.info("🔑 [DEV] Token de reset: ${resetToken.take(20)}...")
            logger.info("⏰ [DEV] Token expira em: $expiresAt")

        } else {
            logger.warn("Tentativa de reset para email não cadastrado ou inativo: $maskedEmail")
        }

        return MessageResponse(
            "Se o email existir em nossa base, você receberá instruções para redefinir sua senha."
        )
    }
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
