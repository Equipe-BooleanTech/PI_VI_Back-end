package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.PasswordResetToken
import edu.fatec.petwise.domain.repository.PasswordResetTokenRepository
import edu.fatec.petwise.infrastructure.persistence.entity.PasswordResetTokenEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaPasswordResetTokenRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Repository
class PasswordResetTokenRepositoryAdapter(
    private val repository: JpaPasswordResetTokenRepository
) : PasswordResetTokenRepository {

    override fun findByTokenAndUsedFalse(token: String): PasswordResetToken? {
        return repository.findByTokenAndUsedFalse(token)?.toDomain()
    }

    override fun findByUserIdAndUsedFalse(userId: UUID): List<PasswordResetToken> {
        return repository.findByUserIdAndUsedFalse(userId).map { it.toDomain() }
    }

    override fun deleteByExpiresAtBefore(date: LocalDateTime): Int {
        return repository.deleteByExpiresAtBefore(date)
    }


    override fun save(token: PasswordResetToken): PasswordResetToken {
        val entity = PasswordResetTokenEntity().apply {
            userId = token.userId
            this.token = token.token
            expiresAt = token.expiresAt
            used = token.used
            usedAt = token.usedAt
            createdAt = token.createdAt
            id = token.id
        }
        return repository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Optional<PasswordResetToken> {
        return repository.findById(id).map { it.toDomain() }
    }

    private fun PasswordResetTokenEntity.toDomain(): PasswordResetToken {
        return PasswordResetToken(
            id = this.id,
            userId = this.userId!!,
            token = this.token!!,
            expiresAt = this.expiresAt!!,
            used = this.used,
            usedAt = this.usedAt,
            createdAt = this.createdAt!!
        )
    }
}
