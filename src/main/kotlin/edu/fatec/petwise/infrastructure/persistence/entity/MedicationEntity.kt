package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "medications")
class MedicationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    
    @Column(nullable = false, name = "pet_id")
    var petId: UUID,
    
    @Column(nullable = false, length = 100)
    var name: String,
    
    @Column(nullable = false, length = 100)
    var dosage: String,
    
    @Column(nullable = false, length = 100)
    var frequency: String,
    
    @Column(nullable = false, name = "start_date")
    var startDate: LocalDate,
    
    @Column(nullable = false, name = "end_date")
    var endDate: LocalDate,
    
    @Column(nullable = false, name = "prescribed_by")
    var prescribedBy: UUID,
    
    @Column(columnDefinition = "TEXT")
    var instructions: String? = null,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
