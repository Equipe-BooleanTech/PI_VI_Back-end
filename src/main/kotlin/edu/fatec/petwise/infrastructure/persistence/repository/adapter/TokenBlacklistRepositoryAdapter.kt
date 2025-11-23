package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.TokenBlacklist
import edu.fatec.petwise.domain.repository.TokenBlacklistRepository
import edu.fatec.petwise.infrastructure.persistence.entity.TokenBlacklistEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaTokenBlacklistRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class TokenBlacklistRepositoryAdapter(
    private val repository: JpaTokenBlacklistRepository
) : TokenBlacklistRepository {

    override fun isTokenBlacklisted(token: String): Boolean {
        return repository.existsByToken(token)
    }

    override fun save(tokenBlacklist: TokenBlacklist): TokenBlacklist {
        val entity = TokenBlacklistEntity().apply {
            token = tokenBlacklist.token
            userId = tokenBlacklist.userId
            expiresAt = tokenBlacklist.expiresAt
            blacklistedAt = tokenBlacklist.blacklistedAt
            reason = tokenBlacklist.reason
            id = tokenBlacklist.id
        }
        return repository.save(entity).toDomain()
    }

    override fun deleteByExpiresAtBefore(date: LocalDateTime): Int {
        return repository.deleteByExpiresAtBefore(date)
    }

    override fun findByUserId(userId: UUID): List<TokenBlacklist> {
        return repository.findByUserId(userId).map { it.toDomain() }
    }

    private fun TokenBlacklistEntity.toDomain(): TokenBlacklist {
        return TokenBlacklist(
            id = this.id,
            token = this.token!!,
            userId = this.userId!!,
            expiresAt = this.expiresAt!!,
            blacklistedAt = this.blacklistedAt!!,
            reason = this.reason
        )
    }
}