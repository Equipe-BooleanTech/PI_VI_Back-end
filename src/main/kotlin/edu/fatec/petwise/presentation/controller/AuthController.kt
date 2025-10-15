package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.AuthResponse
import edu.fatec.petwise.application.dto.LoginRequest
import edu.fatec.petwise.application.dto.RegisterRequest
import edu.fatec.petwise.application.dto.UserResponse
import edu.fatec.petwise.application.usecase.GetUserProfileUseCase
import edu.fatec.petwise.application.usecase.LoginUserUseCase
import edu.fatec.petwise.application.usecase.RegisterUserUseCase
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        logger.info("Requisição de registro recebida para: ${request.email}")
        val response = registerUserUseCase.execute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        logger.info("Requisição de login recebida para: ${request.email}")
        val response = loginUserUseCase.execute(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/profile")
    fun getProfile(authentication: Authentication): ResponseEntity<UserResponse> {
        val userId = authentication.name
        logger.info("Requisição de perfil para usuário: $userId")
        val response = getUserProfileUseCase.execute(userId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Map<String, String>> {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("message" to "Token ausente ou inválido"))
        }

        val token = authHeader.substring(7)

        response.setHeader("Authorization", "")
        response.setHeader("Clear-Site-Data", "\"cookies\"")

        logger.info("Logout realizado com sucesso para token: $token")

        return ResponseEntity.ok(mapOf("message" to "Logout realizado com sucesso"))
    }
}
