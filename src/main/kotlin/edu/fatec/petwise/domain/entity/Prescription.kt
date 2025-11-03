package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.application.dto.PrescriptionResponse
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "prescriptions")
data class Prescription(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "pet_id", nullable = false)
    val petId: UUID,

    @Column(name = "veterinarian", nullable = false, length = 100)
    val veterinarian: String,

    @Column(name = "medical_record_id")
    val medicalRecordId: UUID?,

    @Column(name = "prescription_date", nullable = false)
    val prescriptionDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "instructions", nullable = false, columnDefinition = "TEXT")
    val instructions: String,

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    val diagnosis: String? = null,

    @Column(name = "valid_until")
    val validUntil: LocalDateTime? = null,

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val status: PrescriptionStatus = PrescriptionStatus.ATIVA,

    @Column(name = "active", nullable = false)
    val active: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    enum class PrescriptionStatus {
        ATIVA, EXPIRADA, CANCELADA
    }
    
    fun toPrescriptionResponse(): PrescriptionResponse {
        return PrescriptionResponse(
            id = id,
            petId = petId,
            veterinarian = veterinarian,
            medicalRecordId = medicalRecordId,
            prescriptionDate = prescriptionDate,
            instructions = instructions,
            diagnosis = diagnosis,
            validUntil = validUntil,
            status = status,
            active = active,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}