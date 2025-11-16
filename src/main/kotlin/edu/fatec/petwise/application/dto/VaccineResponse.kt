package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.VaccinationStatus
import edu.fatec.petwise.domain.enums.VaccineType
import java.time.LocalDateTime
import java.util.UUID

data class VaccineResponse(
    val id: UUID?,
    val petId: UUID,
    val veterinarianId: UUID,
    val vaccineType: VaccineType,
    val vaccinationDate: LocalDateTime,
    val nextDoseDate: LocalDateTime? = null,
    val totalDoses: Int,
    val manufacturer: String? = null,
    val observations: String,

    val status: VaccinationStatus,

    val createdAt: LocalDateTime,

    val updatedAt: LocalDateTime
)
