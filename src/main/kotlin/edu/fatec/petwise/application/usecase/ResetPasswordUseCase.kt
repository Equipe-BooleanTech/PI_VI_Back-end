package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.repository.PasswordResetTokenRepository
import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.application.dto.ResetPasswordDto
import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.infrastructure.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
@Service
class ResetPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: ResetPasswordDto): MessageResponse {
        logger.info("Tentativa de redefinição de senha")
        if (!jwtService.validateToken(request.token, "RESET")) {
            logger.warn("Tentativa de reset com token inválido")
            throw BusinessRuleException("Token de reset inválido ou expirado")
        }
        val userId = try {
            UUID.fromString(jwtService.getUserIdFromToken(request.token))
        } catch (e: Exception) {
            logger.error("Erro ao extrair userId do token: ${e.message}")
            throw BusinessRuleException("Token de reset inválido")
        }
        val resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(request.token)
            ?: run {
                logger.warn("Token não encontrado ou já usado: userId=$userId")
                throw BusinessRuleException("Token de reset inválido ou já utilizado")
            }
        resetToken.expiresAt?.let {
            if (it.isBefore(LocalDateTime.now())) {
                logger.warn("Token expirado: userId=$userId, expirou em ${resetToken.expiresAt}")
                throw BusinessRuleException("Token de reset expirado. Solicite um novo reset de senha.")
            }
        }
        resetToken.userId?.let {
            if ((it).equals(userId)) {
                logger.error("Token não pertence ao usuário: tokenUserId=${resetToken.userId}, requestUserId=$userId")
                throw BusinessRuleException("Token inválido")
            }
        }
        val user = userRepository.findById(userId).orElseThrow { EntityNotFoundException("Usuário", userId.toString()) }

        if (!user.active) {
            throw BusinessRuleException("Usuário inativo")
        }
        if (request.newPassword.length < 8) {
            throw BusinessRuleException("A senha deve ter no mínimo 8 caracteres")
        }
        user.passwordHash = passwordEncoder.encode(request.newPassword)
        userRepository.save(user)

        resetToken.used = true
        resetToken.usedAt = LocalDateTime.now()
        passwordResetTokenRepository.save(resetToken)

        logger.info("Senha redefinida com sucesso para usuário: $userId")

        return MessageResponse("Senha redefinida com sucesso. Você já pode fazer login com a nova senha.")
    }
}
