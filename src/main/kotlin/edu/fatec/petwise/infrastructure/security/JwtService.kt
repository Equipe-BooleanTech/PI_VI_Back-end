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
    private var expiration: Long = 86400000

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(userId: String, email: String, userType: UserType): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .subject(userId)
            .claim("email", email)
            .claim("role", userType.name)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserIdFromToken(token: String): String {
        val claims = extractAllClaims(token)
        return claims.subject
    }

    fun getEmailFromToken(token: String): String {
        val claims = extractAllClaims(token)
        return claims["email"] as String
    }

    fun getRoleFromToken(token: String): String {
        val claims = extractAllClaims(token)
        return claims["role"] as String
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
