package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, UUID> {

    fun findByTokenAndUsedFalse(token: String): PasswordResetToken?
    fun findByUserIdAndUsedFalse(userId: UUID): List<PasswordResetToken>
    fun deleteByExpiresAtBefore(date: LocalDateTime): Int
}
