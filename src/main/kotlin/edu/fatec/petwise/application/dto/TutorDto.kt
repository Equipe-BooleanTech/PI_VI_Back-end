package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateTutorRequest(
    @field:NotBlank(message = "{tutor.name.required}")
    @field:Size(min = 3, max = 100, message = "{tutor.name.size}")
    val name: String,
    
    @field:NotBlank(message = "{tutor.cpf.required}")
    @field:Pattern(regexp = "^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$", message = "{tutor.cpf.invalid}")
    val cpf: String,
    
    @field:NotBlank(message = "{tutor.email.required}")
    @field:Email(message = "{tutor.email.invalid}")
    val email: String,
    
    @field:NotBlank(message = "{tutor.phone.required}")
    @field:Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "{tutor.phone.invalid}")
    val phone: String,
    
    val address: String?
)

data class UpdateTutorRequest(
    @field:Size(min = 3, max = 100, message = "{tutor.name.size}")
    val name: String?,
    
    @field:Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "{tutor.phone.invalid}")
    val phone: String?,
    
    val address: String?
)

data class TutorResponse(
    val id: String,
    val name: String,
    val cpf: String,
    val email: String,
    val phone: String,
    val address: String?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
