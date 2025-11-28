package edu.fatec.petwise.infrastructure.security

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.TokenBlacklistRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtService(
    private val tokenBlacklistRepository: TokenBlacklistRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${JWT_SECRET}")
    private lateinit var secret: String

    @Value("\${JWT_EXPIRATION}")
    private var expiration: Long = 86400000 // 1 dia

    @Value("\${JWT_RESET_EXPIRATION:900000}") // 15 minutos padrão
    private var resetExpiration: Long = 900000

    @Value("\${JWT_REFRESH_EXPIRATION:604800000}") // 7 dias padrão
    private var refreshExpiration: Long = 604800000

    @PostConstruct
    fun validateSecretKey() {
        // 🔒 VALIDAÇÃO DE SEGURANÇA: JWT Secret deve ter no mínimo 256 bits (64 caracteres hex)
        if (secret.length < 64) {
            throw IllegalStateException(
                "JWT_SECRET deve ter no mínimo 256 bits (64 caracteres). " +
                        "Tamanho atual: ${secret.length}. " +
                        "Use: openssl rand -hex 64"
            )
        }
        logger.info("JWT Secret validado com sucesso (${secret.length} caracteres)")
    }

    private fun getSigningKey(): SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray())


    fun generateToken(userId: String, email: String, userType: UserType): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .claim("role", userType.name)
            .claim("type", "ACCESS")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    fun generateRefreshToken(userId: String, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshExpiration)

        return Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .claim("type", "REFRESH")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    fun generateResetToken(userId: String, email: String): String {
        val expirationTime = Date(System.currentTimeMillis() + resetExpiration)

        return Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("type", "RESET")
            .setIssuedAt(Date())
            .setExpiration(expirationTime)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }


    fun extractUserId(token: String): String =
        extractAllClaims(token).subject

    fun extractEmail(token: String): String =
        extractAllClaims(token)["email"] as String

    fun extractType(token: String): String =
        extractAllClaims(token)["type"] as String

    fun extractRole(token: String): String =
        extractAllClaims(token)["role"]?.toString() ?: "UNKNOWN"

    fun getUserIdFromToken(token: String): String = extractUserId(token)

    fun getRoleFromToken(token: String): String = extractRole(token)

    fun validateToken(token: String): Boolean = validateToken(token, "ACCESS")

    fun validateToken(token: String, expectedType: String = "ACCESS"): Boolean {
        return try {
            val claims = extractAllClaims(token)
            val now = Date()
            val expiration = claims.expiration ?: run {
                logger.warn("Token validation failed: no expiration claim")
                return false
            }
            val type = claims["type"] as? String ?: run {
                logger.warn("Token validation failed: no type claim")
                return false
            }
            val userId = claims.subject
            val role = claims["role"]?.toString() ?: "UNKNOWN"

            // Check if token is blacklisted
            if (tokenBlacklistRepository.isTokenBlacklisted(token)) {
                // Only log a masked version of the token for security
                val maskedToken = token.take(10) + "..." + token.takeLast(10)
                logger.warn("Token validation failed: token is blacklisted for user $userId (role: $role). Token: $maskedToken")
                return false
            }

            val isExpired = expiration.before(now)
            val typeMatches = type == expectedType
            
            if (isExpired) {
                logger.warn("Token validation failed: token expired for user $userId (role: $role)")
                return false
            }
            
            if (!typeMatches) {
                logger.warn("Token validation failed: expected type $expectedType but got $type for user $userId (role: $role)")
                return false
            }
            
            logger.debug("Token validated successfully for user $userId (role: $role, type: $type)")
            true
        } catch (e: Exception) {
            logger.warn("Token validation failed with exception: ${e.message}")
            false
        }
    }

    fun blacklistToken(token: String, userId: String, reason: String? = null) {
        try {
            val claims = extractAllClaims(token)
            val expiresAt = claims.expiration?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDateTime()
                ?: throw IllegalArgumentException("Token sem data de expiração")

            val tokenBlacklist = edu.fatec.petwise.domain.entity.TokenBlacklist(
                id = null,
                token = token,
                userId = UUID.fromString(userId),
                expiresAt = expiresAt,
                blacklistedAt = java.time.LocalDateTime.now(),
                reason = reason ?: "Logout"
            )

            tokenBlacklistRepository.save(tokenBlacklist)
            logger.info("Token blacklisted for user: $userId")
        } catch (e: Exception) {
            logger.error("Erro ao fazer blacklist do token: ${e.message}")
            throw e
        }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
