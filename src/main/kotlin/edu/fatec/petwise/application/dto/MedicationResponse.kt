package com.petwise.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.*

data class MedicationResponse(
    val id: UUID,
    val petId: UUID,
    val prescriptionId: UUID,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val durationDays: Int? = null,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val startDate: LocalDateTime?,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val endDate: LocalDateTime?,

    val administered: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val administeredAt: LocalDateTime?,

    val administrationNotes: String? = null,
    val sideEffects: String? = null,
    val active: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime
)