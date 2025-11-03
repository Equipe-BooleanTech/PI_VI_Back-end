package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.enums.PetGender
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "pets")
class PetEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    
    @Column(nullable = false, length = 50)
    var name: String,
    
    @Column(nullable = false, length = 50)
    var breed: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var species: PetSpecies,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gender: PetGender,
    
    @Column(nullable = false)
    var age: Int,
    
    @Column(nullable = false)
    var weight: Float,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "health_status")
    var healthStatus: HealthStatus = HealthStatus.GOOD,
    
    @Column(nullable = false, name = "owner_id")
    var ownerId: UUID,
    
    @Column(columnDefinition = "TEXT")
    var healthHistory: String = "",
    
    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,
    
    @Column(nullable = false, name = "is_favorite")
    var isFavorite: Boolean = false,
    
    @Column(name = "next_appointment")
    var nextAppointment: String? = null,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
