package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Prescription
import java.time.LocalDateTime
import java.util.UUID

data class PrescriptionResponse(
    val id: UUID?,
    val petId: UUID,
    val userId: UUID,
    val veterinaryId: UUID,
    val medicalRecordId: UUID?,
    val prescriptionDate: LocalDateTime,
    val instructions: String,
    val diagnosis: String?,
    val validUntil: LocalDateTime?,
    val status: String,
    val medications: String,
    val observations: String,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(prescription: Prescription): PrescriptionResponse {
            return PrescriptionResponse(
                id = prescription.id,
                petId = prescription.petId,
                userId = prescription.userId,
                veterinaryId = prescription.veterinaryId,
                medicalRecordId = prescription.medicalRecordId,
                prescriptionDate = prescription.prescriptionDate,
                instructions = prescription.instructions,
                diagnosis = prescription.diagnosis,
                validUntil = prescription.validUntil,
                status = prescription.status,
                medications = prescription.medications,
                observations = prescription.observations,
                active = prescription.active,
                createdAt = prescription.createdAt,
                updatedAt = prescription.updatedAt
            )
        }
    }
}
