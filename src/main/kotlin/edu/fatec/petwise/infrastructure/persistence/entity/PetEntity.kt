package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
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
    
    @Column
    var weight: Double?,
    
    @Column(nullable = false, name = "tutor_id")
    var tutorId: UUID,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
