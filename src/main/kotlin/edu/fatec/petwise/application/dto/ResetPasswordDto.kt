package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ResetPasswordDto(
    @field:NotBlank(message = "Token é obrigatório")
    val token: String,

    @field:NotBlank(message = "Nova senha é obrigatória")
    @field:Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    val newPassword: String
)
