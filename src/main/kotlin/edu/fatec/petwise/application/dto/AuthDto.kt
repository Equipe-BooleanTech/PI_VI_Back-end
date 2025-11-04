package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.UserType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "{user.fullName.required}")
    @field:Size(min = 3, max = 100, message = "{user.fullName.size}")
    val fullName: String,

    @field:NotBlank(message = "{user.email.required}")
    @field:Email(message = "{user.email.invalid}")
    val email: String,

    @field:NotNull(message = "{user.userType.required}")
    val userType: UserType,

    @field:Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$|^$", message = "{user.cpf.invalid}")
    val cpf: String? = null,

    val crmv: String? = null,

    val specialization: String? = null,

    @field:Pattern(regexp = "^\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$|^$", message = "{user.cnpj.invalid}")
    val cnpj: String? = null,

    val companyName: String? = null,

    @field:NotBlank(message = "{user.phone.required}")
    @field:Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "{user.phone.invalid}")
    val phone: String,

    @field:NotBlank(message = "{user.password.required}")
    @field:Size(min = 6, message = "{user.password.size}")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "{user.email.required}")
    @field:Email(message = "{user.email.invalid}")
    val email: String,
    
    @field:NotBlank(message = "{user.password.required}")
    val password: String
)

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val userId: String,
    val fullName: String,
    val email: String,
    val userType: String,
    val expiresIn: Long
)


data class UserResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val userType: String,
    val cpf: String? = null,
    val crmv: String? = null,
    val specialization: String? = null,
    val cnpj: String? = null,
    val companyName: String? = null,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
