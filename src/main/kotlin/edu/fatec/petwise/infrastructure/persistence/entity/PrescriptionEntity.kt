package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.entity.Prescription
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "prescriptions")
class PrescriptionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "pet_id", nullable = false)
    var petId: UUID,

    @Column(name = "veterinary_id", nullable = false, length = 100)
    var veterinaryId: UUID,

    @Column(name = "medical_record_id")
    var medicalRecordId: UUID? = null,

    @Column(name = "prescription_date", nullable = false)
    var prescriptionDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "instructions", nullable = false, columnDefinition = "TEXT")
    var instructions: String,

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    var diagnosis: String? = null,

    @Column(name = "valid_until")
    var validUntil: LocalDateTime? = null,

    @Column(name = "status", nullable = false, length = 20)
    var status: String = Prescription.PrescriptionStatus.ATIVA.name,

    @Column(name = "medications", nullable = false, columnDefinition = "TEXT")
    var medications: String,

    @Column(name = "observations", nullable = false, columnDefinition = "TEXT")
    var observations: String,

    @Column(name = "active", nullable = false)
    var active: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
