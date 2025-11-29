package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "pet_tags")
class PetTagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 100)
    var tagUid: String,

    @Column(nullable = false, name = "pet_id")
    var petId: UUID,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime,

    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime
)