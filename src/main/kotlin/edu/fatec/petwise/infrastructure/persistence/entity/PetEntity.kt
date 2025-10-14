package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.enums.HealthStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
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
    
    @Column(nullable = false, length = 30)
    var species: String,
    
    @Column(length = 50)
    var breed: String?,
    
    @Column(nullable = false, name = "birth_date")
    var birthDate: LocalDate,
    
    @Column(precision = 5, scale = 2)
    var weight: BigDecimal?,
    
    @Column(nullable = false, name = "owner_id")
    var ownerId: UUID,
    
    @Column(nullable = false, name = "is_favorite")
    var isFavorite: Boolean = false,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "health_status")
    var healthStatus: HealthStatus = HealthStatus.SAUDAVEL,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
