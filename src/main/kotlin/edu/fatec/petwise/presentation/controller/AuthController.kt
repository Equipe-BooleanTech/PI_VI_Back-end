package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.AuthResponse
import edu.fatec.petwise.application.dto.ForgotPasswordDto
import edu.fatec.petwise.application.dto.LoginRequest
import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.application.dto.RegisterRequest
import edu.fatec.petwise.application.dto.ResetPasswordDto
import edu.fatec.petwise.application.dto.UpdateProfileDto
import edu.fatec.petwise.application.usecase.ForgotPasswordUseCase
import edu.fatec.petwise.application.usecase.GetUserProfileUseCase
import edu.fatec.petwise.application.usecase.LoginUserUseCase
import edu.fatec.petwise.application.usecase.LogoutUserUseCase
import edu.fatec.petwise.application.usecase.RefreshTokenUseCase
import edu.fatec.petwise.application.usecase.RegisterUserUseCase
import edu.fatec.petwise.application.usecase.ResetPasswordUseCase
import edu.fatec.petwise.application.usecase.UpdateProfileUseCase
import edu.fatec.petwise.application.usecase.DeleteUserUseCase
import edu.fatec.petwise.application.dto.UpdateProfileResponse
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
    private val updateProfileUseCase: UpdateProfileUseCase, 
    private val deleteUserUseCase: DeleteUserUseCase, 
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase, 
    private val resetPasswordUseCase: ResetPasswordUseCase, 
    private val logoutUserUseCase: LogoutUserUseCase 
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

    
    @PutMapping("/profile")
    fun updateProfile(
        authentication: Authentication,
        @Valid @RequestBody request: UpdateProfileDto
    ): ResponseEntity<UpdateProfileResponse> {
        val userId = UUID.fromString(authentication.name)
        logger.info("Requisição de atualização de perfil para usuário: $userId")
        val response = updateProfileUseCase.execute(userId, request)
        return ResponseEntity.ok(response)
    }

    
    @DeleteMapping("/profile")
    fun deleteProfile(authentication: Authentication): ResponseEntity<MessageResponse> {
        val userId = UUID.fromString(authentication.name)
        logger.info("Requisição de exclusão de perfil para usuário: $userId")
        val response = deleteUserUseCase.execute(userId)
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

    
    @PostMapping("/forgot-password")
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordDto): ResponseEntity<MessageResponse> {
        logger.info("Requisição de forgot password recebida")
        val response = forgotPasswordUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    
    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordDto): ResponseEntity<MessageResponse> {
        logger.info("Requisição de reset password recebida")
        val response = resetPasswordUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("message" to "Token ausente ou inválido"))
        }

        val token = authHeader.substring(7)
        val userId = authentication.name

        try {
            logoutUserUseCase.execute(token, userId)

            response.setHeader("Authorization", "")
            response.setHeader("Clear-Site-Data", "\"cookies\"")

            logger.info("Logout realizado com sucesso - token invalidado no servidor")

            return ResponseEntity.ok(mapOf(
                "message" to "Logout realizado com sucesso",
                "note" to "Token foi invalidado permanentemente no servidor"
            ))
        } catch (e: Exception) {
            logger.error("Erro durante logout: ${e.message}")
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Erro durante logout"))
        }
    }

    
    @DeleteMapping("/clear-blacklist")
    fun clearUserBlacklist(
        authentication: Authentication
    ): ResponseEntity<Map<String, Any>> {
        val userId = UUID.fromString(authentication.name)
        logger.info("Solicitação de limpeza de blacklist para usuário: $userId")

        return try {
            val deletedCount = getUserProfileUseCase.clearUserBlacklist(userId)
            logger.info("Blacklist limpa com sucesso para usuário $userId: $deletedCount tokens removidos")

            ResponseEntity.ok(mapOf(
                "message" to "Blacklist limpa com sucesso",
                "deletedTokens" to deletedCount,
                "note" to "Faça logout e login novamente para obter um novo token"
            ))
        } catch (e: Exception) {
            logger.error("Erro ao limpar blacklist: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Erro ao limpar blacklist", "error" to (e.message ?: "Erro desconhecido")))
        }
    }
}
