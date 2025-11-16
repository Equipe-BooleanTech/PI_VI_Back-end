package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.enums.MedicationStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "medications")
data class MedicationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, name = "user_id")
    val userId: UUID,

    @Column(nullable = false, name = "prescription_id")
    val prescriptionId: UUID,

    @Column(nullable = false, name = "medication_name", length = 100)
    val medicationName: String,

    @Column(nullable = false, length = 100)
    val dosage: String,

    @Column(nullable = false, length = 50)
    val frequency: String,

    @Column(name = "duration_days")
    val durationDays: Int,

    @Column(name = "start_date")
    val startDate: LocalDateTime,

    @Column(name = "end_date")
    val endDate: LocalDateTime,

    @Column(name = "side_effects", columnDefinition = "TEXT")
    val sideEffects: String = "",

    @Enumerated(EnumType.STRING)
    val status: MedicationStatus = MedicationStatus.ACTIVE,

    @Column(nullable = false, name = "created_at", updatable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false, name = "updated_at")
    val updatedAt: LocalDateTime
)
