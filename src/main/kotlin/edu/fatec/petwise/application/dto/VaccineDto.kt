package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class CreateVaccineRequest(
    @field:NotBlank(message = "{vaccine.petId.required}")
    val petId: String,
    
    @field:NotBlank(message = "{vaccine.name.required}")
    @field:Size(min = 2, max = 100, message = "{vaccine.name.size}")
    val name: String,
    
    @field:Size(max = 100, message = "{vaccine.manufacturer.size}")
    val manufacturer: String? = null,
    
    @field:Size(max = 50, message = "{vaccine.batchNumber.size}")
    val batchNumber: String? = null,
    
    @field:NotNull(message = "{vaccine.applicationDate.required}")
    @field:PastOrPresent(message = "{vaccine.applicationDate.pastOrPresent}")
    val applicationDate: LocalDate,
    
    val nextDoseDate: LocalDate? = null,
    
    @field:NotBlank(message = "{vaccine.veterinaryId.required}")
    val veterinaryId: String,
    
    val notes: String? = null
)

data class UpdateVaccineRequest(
    val nextDoseDate: LocalDate? = null,
    val notes: String? = null
)

data class VaccineResponse(
    val id: String,
    val petId: String,
    val name: String,
    val manufacturer: String?,
    val batchNumber: String?,
    val applicationDate: String,
    val nextDoseDate: String?,
    val veterinaryId: String,
    val notes: String?,
    val isDueForNextDose: Boolean,
    val createdAt: String,
    val updatedAt: String
)
