package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.application.dto.MedicalRecordResponse
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "medical_records")
data class MedicalRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "pet_id", nullable = false)
    val petId: UUID,
    @Column(name = "veterinarian", nullable = false, length = 100)
    val veterinarian: String,
    
    @Column(name = "appointment_id")
    val appointmentId: UUID?,
    
    @Column(name = "record_date", nullable = false)
    val recordDate: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "diagnosis", nullable = false, columnDefinition = "TEXT")
    val diagnosis: String,
    
    @Column(name = "treatment", columnDefinition = "TEXT")
    val treatment: String? = null,
    
    @Column(name = "observations", columnDefinition = "TEXT")
    val observations: String? = null,
    
    @Column(name = "vital_signs", columnDefinition = "TEXT")
    val vitalSigns: String? = null,
    
    @Column(name = "weight_kg", precision = 5, scale = 2)
    val weightKg: Double? = null,
    
    @Column(name = "temperature", precision = 3, scale = 1)
    val temperature: Double? = null,
    
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toMedicalRecordResponse(): MedicalRecordResponse {
        return MedicalRecordResponse(
            id = id,
            petId = petId,
            veterinarian = veterinarian,
            appointmentId = appointmentId,
            recordDate = recordDate,
            diagnosis = diagnosis,
            treatment = treatment,
            observations = observations,
            vitalSigns = vitalSigns,
            weightKg = weightKg,
            temperature = temperature,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}