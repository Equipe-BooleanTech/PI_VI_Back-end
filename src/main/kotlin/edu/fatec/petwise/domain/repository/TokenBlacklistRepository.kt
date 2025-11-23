package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.TokenBlacklist
import java.time.LocalDateTime
import java.util.UUID

interface TokenBlacklistRepository {
    fun isTokenBlacklisted(token: String): Boolean
    fun save(tokenBlacklist: TokenBlacklist): TokenBlacklist
    fun deleteByExpiresAtBefore(date: LocalDateTime): Int
    fun findByUserId(userId: UUID): List<TokenBlacklist>
}