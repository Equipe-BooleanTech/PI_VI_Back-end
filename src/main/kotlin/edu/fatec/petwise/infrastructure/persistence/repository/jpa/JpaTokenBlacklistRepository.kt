package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.TokenBlacklistEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaTokenBlacklistRepository : JpaRepository<TokenBlacklistEntity, UUID> {
    fun existsByToken(token: String): Boolean
    fun deleteByExpiresAtBefore(date: LocalDateTime): Int
    fun findByUserId(userId: UUID): List<TokenBlacklistEntity>
    fun deleteByUserId(userId: UUID): Int
}