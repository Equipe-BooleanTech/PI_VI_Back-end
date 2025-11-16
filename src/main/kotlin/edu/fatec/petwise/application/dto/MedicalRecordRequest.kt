package edu.fatec.petwise.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class MedicalRecordRequest(
    @field:NotNull(message = "Pet ID é obrigatório")
    val petId: String,
    
    @field:NotBlank(message = "Veterinário é obrigatório")
    val veterinarian: String,
    
    val appointmentId: String? = null,
    
    @field:NotNull(message = "Data do registro é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val recordDate: LocalDateTime = LocalDateTime.now(),
    
    @field:NotBlank(message = "Diagnóstico é obrigatório")
    val diagnosis: String,
    
    val treatment: String? = null,
    
    val observations: String? = null,
    
    val vitalSigns: String? = null,
    
    val weightKg: Double? = null,
    
    val temperature: Double? = null
)
