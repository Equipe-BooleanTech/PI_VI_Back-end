package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

class PasswordResetToken(
    val id: UUID?,
    val userId: UUID,
    val token: String,
    val expiresAt: LocalDateTime,
    var used: Boolean = false,
    var usedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime
)
