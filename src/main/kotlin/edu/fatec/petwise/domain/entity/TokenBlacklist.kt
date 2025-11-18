package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

class TokenBlacklist(
    val id: UUID?,
    val token: String,
    val userId: UUID,
    val expiresAt: LocalDateTime,
    val blacklistedAt: LocalDateTime,
    val reason: String? = null
)