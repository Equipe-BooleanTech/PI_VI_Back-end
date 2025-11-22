package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

class Prescription(
    var id: UUID? = null,
    val userId: UUID,
    val petId: UUID,
    val veterinaryId: UUID,
    val medicalRecordId: UUID?,
    val prescriptionDate: LocalDateTime,
    var instructions: String,
    var diagnosis: String?,
    var validUntil: LocalDateTime?,
    var status: String = PrescriptionStatus.ATIVA.name,
    var medications: String,
    var observations: String,
    var active: Boolean = true,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    enum class PrescriptionStatus {
        ATIVA, EXPIRADA, CANCELADA
    }
}
