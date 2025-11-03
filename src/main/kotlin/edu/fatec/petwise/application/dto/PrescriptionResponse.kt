package edu.fatec.petwise.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import edu.fatec.petwise.domain.entity.Prescription
import java.time.LocalDateTime
import java.util.*

data class PrescriptionResponse(
    val id: UUID,
    val petId: UUID,
    val veterinarian: String,
    val medicalRecordId: UUID?,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val prescriptionDate: LocalDateTime,

    val instructions: String,
    val diagnosis: String? = null,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val validUntil: LocalDateTime?,

    val status: Prescription.PrescriptionStatus,
    val active: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime
)