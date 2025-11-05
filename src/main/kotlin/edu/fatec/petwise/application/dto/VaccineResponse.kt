package edu.fatec.petwise.application.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class VaccineResponse(
    val id: UUID,
    val petId: UUID,
    val vaccineTypeId: UUID,
    val veterinarian: String,

    @JsonFormat(pattern = "yyyy-MM-dd")
    val vaccinationDate: LocalDate,

    val batchNumber: String? = null,
    val manufacturer: String? = null,
    val doseNumber: Int,
    val totalDoses: Int? = null,

    @JsonFormat(pattern = "yyyy-MM-dd")
    val validUntil: LocalDate?,

    val siteOfInjection: String? = null,
    val reactions: String? = null,
    val observations: String? = null,
    val active: Boolean,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime
)