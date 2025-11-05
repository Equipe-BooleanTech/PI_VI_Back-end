package edu.fatec.petwise.domain.entity

import com.petwise.dto.MedicationResponse
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "medications")
data class Medication(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "pet_id", nullable = false)
    val petId: UUID,

    @Column(name = "prescription_id", nullable = false)
    val prescriptionId: UUID,
    
    @Column(name = "medication_name", nullable = false, length = 100)
    val medicationName: String,
    
    @Column(name = "dosage", nullable = false, length = 100)
    val dosage: String,
    
    @Column(name = "frequency", nullable = false, length = 50)
    val frequency: String,
    
    @Column(name = "duration_days")
    val durationDays: Int? = null,
    
    @Column(name = "start_date")
    val startDate: LocalDateTime? = null,
    
    @Column(name = "end_date")
    val endDate: LocalDateTime? = null,
    
    @Column(name = "administered", nullable = false)
    val administered: Boolean = false,
    
    @Column(name = "administered_at")
    val administeredAt: LocalDateTime? = null,
    
    @Column(name = "administration_notes", columnDefinition = "TEXT")
    val administrationNotes: String? = null,
    
    @Column(name = "side_effects", columnDefinition = "TEXT")
    val sideEffects: String? = null,
    
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toMedicationResponse(): MedicationResponse {
        return MedicationResponse(
            id = id,
            petId = petId,
            prescriptionId = prescriptionId,
            medicationName = medicationName,
            dosage = dosage,
            frequency = frequency,
            durationDays = durationDays,
            startDate = startDate,
            endDate = endDate,
            administered = administered,
            administeredAt = administeredAt,
            administrationNotes = administrationNotes,
            sideEffects = sideEffects,
            active = active,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}