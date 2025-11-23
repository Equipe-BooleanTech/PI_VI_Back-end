package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.enums.VaccinationStatus
import edu.fatec.petwise.domain.enums.VaccineType
import java.time.LocalDateTime
import java.util.UUID

class Vaccine(
    var id: UUID? = null,
    val petId: UUID,
    val veterinarianId: UUID,
    var vaccineType: VaccineType,
    var vaccinationDate: LocalDateTime,
    var nextDoseDate: LocalDateTime? = null,
    var totalDoses: Int,
    var manufacturer: String? = null,
    var observations: String = "",
    var status: VaccinationStatus,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    fun toVaccineResponse(): VaccineResponse {
        return VaccineResponse(
            id = id,
            petId = petId,
            veterinarianId = veterinarianId,
            vaccineType = vaccineType,
            vaccinationDate = vaccinationDate,
            nextDoseDate = nextDoseDate,
            totalDoses = totalDoses,
            manufacturer = manufacturer,
            observations = observations,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    data class VaccineFilterOptions(
        val petId: UUID? = null,
        val vaccineType: VaccineType? = null,
        val status: VaccinationStatus? = null,
        val startDate: LocalDateTime? = null,
        val endDate: LocalDateTime? = null,
        val searchQuery: String = ""
    )
}
