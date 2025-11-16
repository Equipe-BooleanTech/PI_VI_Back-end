package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.PasswordResetTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaPasswordResetTokenRepository : JpaRepository<PasswordResetTokenEntity, UUID> {
    fun findByTokenAndUsedFalse(token: String): PasswordResetTokenEntity?
    fun findByUserIdAndUsedFalse(userId: UUID): List<PasswordResetTokenEntity>
    fun deleteByExpiresAtBefore(date: LocalDateTime): Int
}
