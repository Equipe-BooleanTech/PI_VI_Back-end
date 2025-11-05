package edu.fatec.petwise.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime
import java.util.UUID

data class MedicationRequest(
    @field:NotNull(message = "Prescription ID é obrigatório")
    val prescriptionId: UUID,
    
    @field:NotBlank(message = "Nome da medicação é obrigatório")
    val medicationName: String,
    
    @field:NotBlank(message = "Dosagem é obrigatória")
    val dosage: String,
    
    @field:NotBlank(message = "Frequência é obrigatória")
    val frequency: String,
    
    @field:Positive(message = "Duração em dias deve ser positiva")
    val durationDays: Int? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startDate: LocalDateTime? = null,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endDate: LocalDateTime? = null,
    
    val administrationNotes: String? = null,
    
    val sideEffects: String? = null
)