package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.application.dto.VaccineTypeResponse
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "vaccine_types")
data class VaccineType(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    
    @Column(name = "species", nullable = false, length = 50)
    val species: String,
    
    @Column(name = "vaccine_name", nullable = false, length = 100)
    val vaccineName: String,
    
    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,
    
    @Column(name = "manufacturer", length = 100)
    val manufacturer: String? = null,
    
    @Column(name = "doses_required", nullable = false)
    val dosesRequired: Int = 1,
    
    @Column(name = "booster_interval_months")
    val boosterIntervalMonths: Int? = null,
    
    @Column(name = "age_restriction_months")
    val ageRestrictionMonths: Int? = null,
    
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toVaccineTypeResponse(): VaccineTypeResponse {
        return VaccineTypeResponse(
            id = id,
            species = species,
            vaccineName = vaccineName,
            description = description,
            manufacturer = manufacturer,
            dosesRequired = dosesRequired,
            boosterIntervalMonths = boosterIntervalMonths,
            ageRestrictionMonths = ageRestrictionMonths,
            active = active
        )
    }
}