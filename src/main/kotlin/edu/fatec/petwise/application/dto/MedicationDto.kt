package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateMedicationRequest(
    @field:NotBlank(message = "{medication.petId.required}")
    val petId: String,
    
    @field:NotBlank(message = "{medication.name.required}")
    @field:Size(min = 2, max = 100, message = "{medication.name.size}")
    val name: String,
    
    @field:NotBlank(message = "{medication.dosage.required}")
    @field:Size(max = 100, message = "{medication.dosage.size}")
    val dosage: String,
    
    @field:NotBlank(message = "{medication.frequency.required}")
    @field:Size(max = 100, message = "{medication.frequency.size}")
    val frequency: String,
    
    @field:NotNull(message = "{medication.startDate.required}")
    val startDate: LocalDate,
    
    @field:NotNull(message = "{medication.endDate.required}")
    val endDate: LocalDate,
    
    @field:NotBlank(message = "{medication.prescribedBy.required}")
    val prescribedBy: String,
    
    val instructions: String? = null
)

data class UpdateMedicationRequest(
    @field:Size(max = 100, message = "{medication.dosage.size}")
    val dosage: String? = null,
    
    @field:Size(max = 100, message = "{medication.frequency.size}")
    val frequency: String? = null,
    
    val instructions: String? = null
)

data class MedicationResponse(
    val id: String,
    val petId: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String,
    val prescribedBy: String,
    val instructions: String?,
    val active: Boolean,
    val isExpired: Boolean,
    val createdAt: String,
    val updatedAt: String
)
