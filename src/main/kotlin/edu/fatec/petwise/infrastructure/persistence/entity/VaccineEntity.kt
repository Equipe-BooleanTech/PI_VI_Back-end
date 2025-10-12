package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
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
    
    @Column(nullable = false, length = 100)
    var name: String,
    
    @Column(length = 100)
    var manufacturer: String? = null,
    
    @Column(name = "batch_number", length = 50)
    var batchNumber: String? = null,
    
    @Column(nullable = false, name = "application_date")
    var applicationDate: LocalDate,
    
    @Column(name = "next_dose_date")
    var nextDoseDate: LocalDate? = null,
    
    @Column(nullable = false, name = "veterinary_id")
    var veterinaryId: UUID,
    
    @Column(columnDefinition = "TEXT")
    var notes: String? = null,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
