package edu.fatec.petwise.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.*

data class MedicalRecordResponse(
    val id: UUID,
    val petId: UUID,
    val veterinarian: String,
    val appointmentId: UUID?,

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