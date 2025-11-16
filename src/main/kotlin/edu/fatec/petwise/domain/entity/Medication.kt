package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.MedicationStatus
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

class Medication(

    var id: UUID? = null,

    val userId: UUID,

    var prescriptionId: UUID,

    var medicationName: String,

    var dosage: String,

    var frequency: String,

    var durationDays: Int,

    var startDate: LocalDateTime,

    var endDate: LocalDateTime,

    var sideEffects: String = "",

    var status: MedicationStatus = MedicationStatus.ACTIVE,

    val createdAt: LocalDateTime,

    var updatedAt: LocalDateTime
)

data class MedicationFilterOptions(
    val petId: UUID? = null,
    val medicationName: String? = null,
    val status: MedicationStatus? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val searchQuery: String? = null
)
