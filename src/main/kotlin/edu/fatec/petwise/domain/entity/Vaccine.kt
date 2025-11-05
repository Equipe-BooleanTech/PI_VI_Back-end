package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.application.dto.VaccineResponse
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "vaccines")
data class Vaccine(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "pet_id", nullable = false)
    val petId: UUID,
    
    @Column(name = "vaccine_type_id", nullable = false)
    val vaccineTypeId: UUID,
    
    @Column(name = "veterinarian", nullable = false, length = 100)
    val veterinarian: String,
    
    @Column(name = "vaccination_date", nullable = false)
    val vaccinationDate: LocalDate,
    
    @Column(name = "batch_number", length = 50)
    val batchNumber: String? = null,
    
    @Column(name = "manufacturer", length = 100)
    val manufacturer: String? = null,
    
    @Column(name = "dose_number", nullable = false)
    val doseNumber: Int = 1,
    
    @Column(name = "total_doses")
    val totalDoses: Int? = null,
    
    @Column(name = "valid_until")
    val validUntil: LocalDate? = null,
    
    @Column(name = "site_of_injection", length = 100)
    val siteOfInjection: String? = null,
    
    @Column(name = "reactions", columnDefinition = "TEXT")
    val reactions: String? = null,
    
    @Column(name = "observations", columnDefinition = "TEXT")
    val observations: String? = null,
    
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toVaccineResponse(): VaccineResponse {
        return VaccineResponse(
            id = id,
            petId = petId,
            vaccineTypeId = vaccineTypeId,
            veterinarian = veterinarian,
            vaccinationDate = vaccinationDate,
            batchNumber = batchNumber,
            manufacturer = manufacturer,
            doseNumber = doseNumber,
            totalDoses = totalDoses,
            validUntil = validUntil,
            siteOfInjection = siteOfInjection,
            reactions = reactions,
            observations = observations,
            active = active,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}