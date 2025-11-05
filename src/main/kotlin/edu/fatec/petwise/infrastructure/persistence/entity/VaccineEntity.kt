package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.enums.VaccineType
import edu.fatec.petwise.domain.enums.VaccinationStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "vaccines")
class VaccineEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    
    @Column(nullable = false, name = "pet_id")
    var petId: UUID,
    
    @Column(nullable = false, name = "vaccine_name", length = 100)
    var vaccineName: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "vaccine_type")
    var vaccineType: VaccineType,
    
    @Column(nullable = false, name = "application_date")
    var applicationDate: LocalDateTime,
    
    @Column(name = "next_dose_date")
    var nextDoseDate: LocalDateTime,
    
    @Column(nullable = false, name = "dose_number")
    var doseNumber: Int,
    
    @Column(nullable = false, name = "total_doses")
    var totalDoses: Int,
    
    @Column(nullable = false, name = "veterinary_id")
    var veterinaryId: UUID,
    
    @Column(nullable = false, name = "clinic_name", length = 100)
    var clinicName: String,
    
    @Column(nullable = false, name = "batch_number", length = 50)
    var batchNumber: String,
    
    @Column(nullable = false, length = 100)
    var manufacturer: String,
    
    @Column(columnDefinition = "TEXT")
    var observations: String = "",
    
    @Column(columnDefinition = "TEXT", name = "side_effects")
    var sideEffects: String = "",
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: VaccinationStatus,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
