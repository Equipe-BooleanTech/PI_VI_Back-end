package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tutors")
class TutorEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    
    @Column(nullable = false, length = 100)
    var name: String,
    
    @Column(nullable = false, unique = true, length = 11)
    var cpf: String,
    
    @Column(nullable = false, unique = true, length = 100)
    var email: String,
    
    @Column(nullable = false, length = 20)
    var phone: String,
    
    @Column(length = 200)
    var address: String?,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
