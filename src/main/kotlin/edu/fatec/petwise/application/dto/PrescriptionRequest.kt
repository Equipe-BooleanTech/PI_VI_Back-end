package com.petwise.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Future
import java.time.LocalDateTime

data class PrescriptionRequest(
    @field:NotNull(message = "Pet ID é obrigatório")
    val petId: Long,
    
    @field:NotBlank(message = "Veterinário é obrigatório")
    val veterinarian: String,
    
    val medicalRecordId: Long? = null,
    
    @field:NotNull(message = "Data da prescrição é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val prescriptionDate: LocalDateTime = LocalDateTime.now(),
    
    @field:NotBlank(message = "Instruções são obrigatórias")
    val instructions: String,
    
    val diagnosis: String? = null,
    
    @field:Future(message = "Data de validade deve ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val validUntil: LocalDateTime? = null
)