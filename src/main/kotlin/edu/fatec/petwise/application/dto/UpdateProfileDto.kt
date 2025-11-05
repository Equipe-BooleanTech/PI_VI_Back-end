package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.Email

data class UpdateProfileDto(
    val fullName: String? = null,

    @field:Email(message = "Email inválido")
    val email: String? = null,

    val phone: String? = null,

    val cpf: String? = null,

    val crmv: String? = null,

    val specialization: String? = null,

    val cnpj: String? = null,
    val companyName: String? = null
)