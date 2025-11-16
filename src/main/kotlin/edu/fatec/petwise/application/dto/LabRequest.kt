package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class LabRequest(
    @field:NotBlank(message = "Nome do laboratório é obrigatório")
    val name: String,

    val contactInfo: String?
)
