package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.UUID

data class PrescriptionRequest(
    @field:NotNull(message = "Pet ID é obrigatório")
    val petId: UUID,

    @field:NotNull(message = "Veterinário é obrigatório")
    val veterinarian: UUID,

    val medicalRecordId: UUID? = null,

    @field:NotNull(message = "Data da prescrição é obrigatória")
    val prescriptionDate: LocalDateTime,

    @field:NotBlank(message = "Instruções são obrigatórias")
    val instructions: String,

    val diagnosis: String? = null,

    val validUntil: LocalDateTime? = null,

    val medications: String? = null,

    val observations: String? = null
)

data class UpdatePrescriptionRequest(
    val instructions: String? = null,

    val diagnosis: String? = null,

    val validUntil: LocalDateTime? = null,

    val status: String? = null,

    val medications: String? = null,

    val observations: String? = null,

    val active: Boolean? = null
)
