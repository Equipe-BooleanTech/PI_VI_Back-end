package edu.fatec.petwise.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class MedicalRecordResponse(
    val id: String,
    val petId: String,
    val veterinarian: String,
    val appointmentId: String?,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val recordDate: LocalDateTime,

    val diagnosis: String,
    val treatment: String? = null,
    val observations: String? = null,
    val vitalSigns: String? = null,
    val weightKg: Double? = null,
    val temperature: Double? = null,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime
)
