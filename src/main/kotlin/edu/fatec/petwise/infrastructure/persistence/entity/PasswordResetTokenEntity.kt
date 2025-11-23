package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "password_reset_tokens")
data class PasswordResetTokenEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false)
    var userId: UUID? = null,

    @Column(nullable = false, length = 500)
    var token: String? = null,

    @Column(nullable = false)
    var expiresAt: LocalDateTime? = null,

    @Column(nullable = false)
    var used: Boolean = false,

    @Column
    var usedAt: LocalDateTime? = null,

    @Column(nullable = false)
    var createdAt: LocalDateTime? = null,
) {
    constructor() : this(null, null, null, null, false, null, null)
}
