package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AuthResponse
import edu.fatec.petwise.application.dto.LoginRequest
import edu.fatec.petwise.application.dto.RegisterRequest
import edu.fatec.petwise.application.dto.UserResponse
import edu.fatec.petwise.domain.entity.User
import edu.fatec.petwise.domain.entity.UserType
import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.DuplicateEntityException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import edu.fatec.petwise.infrastructure.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    @Value("\${JWT_EXPIRATION}") private val jwtExpiration: Long
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: RegisterRequest): AuthResponse {
        logger.info("Registrando novo usuário: ${request.email}")

        if (userRepository.existsByEmail(request.email)) {
            logger.warn("Tentativa de registro com email duplicado: ${request.email}")
            throw DuplicateEntityException("Já existe um usuário cadastrado com este email")
        }

        validateUserTypeSpecificFields(request)

        val user = User(
            fullName = request.fullName,
            email = Email(request.email),
            phone = Telefone(request.phone),
            passwordHash = passwordEncoder.encode(request.password),
            userType = request.userType,
            cpf = request.cpf?.replace(Regex("[^0-9]"), ""),
            crmv = request.crmv,
            specialization = request.specialization,
            cnpj = request.cnpj?.replace(Regex("[^0-9]"), ""),
            companyName = request.companyName
        )

        val savedUser = userRepository.save(user)
        logger.info("Usuário registrado com sucesso. ID: ${savedUser.id}")

        val token = jwtService.generateToken(
            userId = savedUser.id.toString(),
            email = savedUser.email.value,
            userType = savedUser.userType
        )

        return AuthResponse(
            token = token,
            userId = savedUser.id.toString(),
            fullName = savedUser.fullName,
            email = savedUser.email.value,
            userType = savedUser.userType.name,
            expiresIn = jwtExpiration
        )
    }

    private fun validateUserTypeSpecificFields(request: RegisterRequest) {
        when (request.userType) {
            UserType.OWNER -> {
                if (request.cpf.isNullOrBlank()) {
                    throw BusinessRuleException("CPF é obrigatório para tutores")
                }
                val cleanCpf = request.cpf.replace(Regex("[^0-9]"), "")
                if (userRepository.existsByCpf(cleanCpf)) {
                    throw DuplicateEntityException("Já existe um tutor cadastrado com este CPF")
                }
            }
            UserType.VETERINARY -> {
                if (request.crmv.isNullOrBlank()) {
                    throw BusinessRuleException("CRMV é obrigatório para veterinários")
                }
                if (request.specialization.isNullOrBlank()) {
                    throw BusinessRuleException("Especialização é obrigatória para veterinários")
                }
                if (userRepository.existsByCrmv(request.crmv)) {
                    throw DuplicateEntityException("Já existe um veterinário cadastrado com este CRMV")
                }
            }
            UserType.PHARMACY -> {
                if (request.cnpj.isNullOrBlank()) {
                    throw BusinessRuleException("CNPJ é obrigatório para farmácias")
                }
                if (request.companyName.isNullOrBlank()) {
                    throw BusinessRuleException("Nome da empresa é obrigatório para farmácias")
                }
                val cleanCnpj = request.cnpj.replace(Regex("[^0-9]"), "")
                if (userRepository.existsByCnpj(cleanCnpj)) {
                    throw DuplicateEntityException("Já existe uma farmácia cadastrada com este CNPJ")
                }
            }
            UserType.ADMIN -> {}
        }
    }
}

@Service
class LoginUserUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    @Value("\${JWT_EXPIRATION}") private val jwtExpiration: Long
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: LoginRequest): AuthResponse {
        logger.info("Tentativa de login para: ${request.email}")

        val user = userRepository.findByEmail(request.email)
            ?: throw EntityNotFoundException("Usuário", request.email)

        if (!user.active) {
            logger.warn("Tentativa de login com usuário inativo: ${request.email}")
            throw BusinessRuleException("Usuário inativo")
        }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            logger.warn("Tentativa de login com senha incorreta: ${request.email}")
            throw BusinessRuleException("Email ou senha incorretos")
        }

        logger.info("Login realizado com sucesso: ${request.email}")

        val token = jwtService.generateToken(
            userId = user.id.toString(),
            email = user.email.value,
            userType = user.userType
        )

        return AuthResponse(
            token = token,
            userId = user.id.toString(),
            fullName = user.fullName,
            email = user.email.value,
            userType = user.userType.name,
            expiresIn = jwtExpiration
        )
    }
}

@Service
class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String): UserResponse {
        logger.info("Buscando perfil do usuário: $userId")
        
        val user = userRepository.findById(java.util.UUID.fromString(userId))
            ?: throw EntityNotFoundException("Usuário", userId)
        
        return user.toResponse()
    }
}

private fun User.toResponse() = UserResponse(
    id = this.id.toString(),
    fullName = this.fullName,
    email = this.email.value,
    phone = this.phone.value,
    userType = this.userType.name,
    cpf = this.cpf,
    crmv = this.crmv,
    specialization = this.specialization,
    cnpj = this.cnpj,
    companyName = this.companyName,
    active = this.active,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
