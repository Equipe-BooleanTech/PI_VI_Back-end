package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "token_blacklist")
data class TokenBlacklistEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, length = 1000)
    var token: String? = null,

    @Column(nullable = false)
    var userId: UUID? = null,

    @Column(nullable = false)
    var expiresAt: LocalDateTime? = null,

    @Column(nullable = false)
    var blacklistedAt: LocalDateTime? = null,

    @Column(length = 255)
    var reason: String? = null
) {
    constructor() : this(null, null, null, null, null, null)
}