package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.entity.AppointmentStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "appointments")
class AppointmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    
    @Column(nullable = false, name = "pet_id")
    var petId: UUID,
    
    @Column(nullable = false, name = "veterinary_id")
    var veterinaryId: UUID,
    
    @Column(nullable = false, name = "owner_id")
    var ownerId: UUID,
    
    @Column(nullable = false, name = "scheduled_date")
    var scheduledDate: LocalDateTime,
    
    @Column(nullable = false, length = 500)
    var reason: String,
    
    @Column(columnDefinition = "TEXT")
    var notes: String? = null,
    
    @Column(columnDefinition = "TEXT")
    var diagnosis: String? = null,
    
    @Column(columnDefinition = "TEXT")
    var treatment: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
