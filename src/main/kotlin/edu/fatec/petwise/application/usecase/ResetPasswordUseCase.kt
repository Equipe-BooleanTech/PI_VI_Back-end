package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.infrastructure.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class ResetPasswordUseCase(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(token: String, newPassword: String) {
        val email = jwtService.extractEmail(token)
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Usuário não encontrado")

        user.passwordHash = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        logger.info("Senha redefinida para usuário $email")
    }
}
