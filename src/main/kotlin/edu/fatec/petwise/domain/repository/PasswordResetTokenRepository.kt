package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.PasswordResetToken
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

interface PasswordResetTokenRepository {
    fun findByTokenAndUsedFalse(token: String): PasswordResetToken?
    fun findByUserIdAndUsedFalse(userId: UUID): List<PasswordResetToken>
    fun deleteByExpiresAtBefore(date: LocalDateTime): Int
    fun save(token: PasswordResetToken): PasswordResetToken
    fun findById(id: UUID): Optional<PasswordResetToken>
}
