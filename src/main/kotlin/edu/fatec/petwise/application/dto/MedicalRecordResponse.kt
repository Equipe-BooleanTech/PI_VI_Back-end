package edu.fatec.petwise.application.dto

import java.time.LocalDateTime

data class MedicalRecordResponse(
    val id: String,
    val petId: String,
    val veterinarian: String,
    val appointmentId: String?,

    val recordDate: LocalDateTime,

    val diagnosis: String,
    val treatment: String? = null,
    val observations: String? = null,
    val vitalSigns: String? = null,
    val weightKg: Double? = null,
    val temperature: Double? = null,

    val createdAt: LocalDateTime,

    val updatedAt: LocalDateTime
)
