package edu.fatec.petwise.infrastructure.security

import edu.fatec.petwise.domain.entity.UserType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {

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
            val expiration = claims.expiration ?: return false
            val type = claims["type"] as? String ?: return false
            !expiration.before(now) && type == expectedType
        } catch (e: Exception) {
            logger.warn("Token inválido: ${e.message}")
            false
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
