package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.usecase.ForgotPasswordUseCase
import edu.fatec.petwise.application.usecase.GetUserProfileUseCase
import edu.fatec.petwise.application.usecase.LoginUserUseCase
import edu.fatec.petwise.application.usecase.RefreshTokenUseCase
import edu.fatec.petwise.application.usecase.RegisterUserUseCase
import edu.fatec.petwise.application.usecase.ResetPasswordUseCase
import edu.fatec.petwise.application.usecase.UpdateProfileUseCase
import edu.fatec.petwise.application.dto.*
import edu.fatec.petwise.domain.entity.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.Optional
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase, // NOVO
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase, // NOVO
    private val resetPasswordUseCase: ResetPasswordUseCase // NOVO
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        logger.info("Requisição de registro recebida")
        val response = registerUserUseCase.execute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        logger.info("Requisição de login recebida")
        val response = loginUserUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/profile")
    fun getProfile(authentication: Authentication): ResponseEntity<Optional<User>> {
        val userId = UUID.fromString(authentication.name)
        logger.info("Requisição de perfil para usuário: $userId")
        val response = getUserProfileUseCase.execute(userId)
        return ResponseEntity.ok(response)
    }

    /**
     * ✨ NOVO - Sprint 1
     * Atualiza perfil do usuário autenticado
     * Permite atualização parcial (apenas campos enviados são atualizados)
     */
    @PutMapping("/profile")
    fun updateProfile(
        authentication: Authentication,
        @Valid @RequestBody request: UpdateProfileDto
    ): ResponseEntity<UserResponse> {
        val userId = UUID.fromString(authentication.name)
        logger.info("Requisição de atualização de perfil para usuário: $userId")
        val response = updateProfileUseCase.execute(userId, request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: Map<String, String>): ResponseEntity<AuthResponse> {
        val refreshToken = request["refreshToken"]
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null)

        logger.info("Requisição de refresh token recebida")
        val response = refreshTokenUseCase.execute(refreshToken)
        return ResponseEntity.ok(response)
    }

    /**
     * ✨ NOVO - Sprint 1
     * Solicita redefinição de senha
     * Gera token de reset e simula envio de email
     *
     * 🔒 SEGURANÇA: Sempre retorna mesma mensagem (não revela se email existe)
     */
    @PostMapping("/forgot-password")
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordDto): ResponseEntity<MessageResponse> {
        logger.info("Requisição de forgot password recebida")
        val response = forgotPasswordUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    /**
     * ✨ NOVO - Sprint 1
     * Redefine senha usando token de reset
     * Valida token e atualiza senha do usuário
     */
    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordDto): ResponseEntity<MessageResponse> {
        logger.info("Requisição de reset password recebida")
        val response = resetPasswordUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("message" to "Token ausente ou inválido"))
        }


        response.setHeader("Authorization", "")
        response.setHeader("Clear-Site-Data", "\"cookies\"")

        logger.info("Logout realizado - token invalidado no cliente")

        return ResponseEntity.ok(mapOf(
            "message" to "Logout realizado com sucesso",
            "note" to "Token será válido até expiração. Para revogação imediata, implemente blacklist."
        ))
    }
}
