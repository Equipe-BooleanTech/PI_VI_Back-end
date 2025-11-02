package edu.fatec.petwise.infrastructure.security

import edu.fatec.petwise.domain.entity.UserType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {

    @Value("\${JWT_SECRET}")
    private lateinit var secret: String

    @Value("\${JWT_EXPIRATION}")
    private var expiration: Long = 86400000 // 1 dia

    @Value("\${JWT_RESET_EXPIRATION:900000}") // 15 minutos padrão
    private var resetExpiration: Long = 900000

    @Value("\${JWT_REFRESH_EXPIRATION:604800000}") // 7 dias padrão
    private var refreshExpiration: Long = 604800000

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
        val now = Date()
        val expiryDate = Date(now.time + resetExpiration)

        return Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .claim("type", "RESET")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
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

    fun validateToken(token: String, expectedType: String = "ACCESS"): Boolean {
        return try {
            val claims = extractAllClaims(token)
            val now = Date()
            val expiration = claims.expiration ?: return false
            val type = claims["type"] as? String ?: return false
            !expiration.before(now) && type == expectedType
        } catch (e: Exception) {
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
