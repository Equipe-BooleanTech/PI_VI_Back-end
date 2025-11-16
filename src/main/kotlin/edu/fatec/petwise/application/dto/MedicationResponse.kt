package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Medication
import edu.fatec.petwise.domain.enums.MedicationStatus
import java.time.LocalDateTime
import java.util.UUID

data class MedicationResponse(
    val id: UUID?,
    val userId: UUID,
    val prescriptionId: UUID,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val durationDays: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val sideEffects: String,
    val status: MedicationStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(medication: Medication): MedicationResponse {
            return MedicationResponse(
                id = medication.id,
                userId = medication.userId,
                prescriptionId = medication.prescriptionId,
                medicationName = medication.medicationName,
                dosage = medication.dosage,
                frequency = medication.frequency,
                durationDays = medication.durationDays,
                startDate = medication.startDate,
                endDate = medication.endDate,
                sideEffects = medication.sideEffects,
                status = medication.status,
                createdAt = medication.createdAt,
                updatedAt = medication.updatedAt
            )
        }
    }
}
