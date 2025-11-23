package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.Size

data class UpdateHealthStatusRequest(
    @field:Size(max = 20, message = "Status de saúde deve ter no máximo 20 caracteres")
    val healthStatus: String,

    @field:Size(max = 1000, message = "Notas devem ter no máximo 1000 caracteres")
    val notes: String? = null
)
