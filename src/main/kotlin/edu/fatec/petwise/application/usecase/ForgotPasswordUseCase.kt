package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.infrastructure.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class ForgotPasswordUseCase(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val mailSender: JavaMailSender
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(email: String) {
        val user = userRepository.findByEmail(email)
            ?: return logger.warn("Usuário não encontrado: $email")

        val resetToken = jwtService.generateResetToken(user.id.toString(), email)

        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(email)
        helper.setSubject("Redefinição de Senha")
        helper.setText(
            "Olá ${user.fullName},<br><br>Clique no link abaixo para redefinir sua senha:<br>" +
                    "<a href='https://petwise.com/reset-password?token=$resetToken'>Redefinir Senha</a>",
            true
        )
        mailSender.send(message)
        logger.info("E-mail de redefinição enviado para $email")
    }
}
