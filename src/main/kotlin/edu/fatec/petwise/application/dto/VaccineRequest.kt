package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDate
import java.util.UUID

data class VaccineRequest(
    @field:NotNull(message = "Vaccine Type ID é obrigatório")
    val vaccineTypeId: UUID,
    
    @field:NotBlank(message = "Veterinário é obrigatório")
    val veterinarian: String,
    
    @field:NotNull(message = "Data da vacinação é obrigatória")
    val vaccinationDate: LocalDate,
    
    val batchNumber: String? = null,
    val manufacturer: String? = null,
    
    @field:Positive(message = "Número da dose deve ser positivo")
    val doseNumber: Int = 1,
    
    val totalDoses: Int? = null,
    val validUntil: LocalDate? = null,
    val siteOfInjection: String? = null,
    val reactions: String? = null,
    val observations: String? = null
)