package edu.fatec.petwise.infrastructure.security

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
class TokenBlacklistService {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    private val blacklistedTokens = ConcurrentHashMap<String, Long>()
    
    init {
        val scheduler = Executors.newSingleThreadScheduledExecutor()
        scheduler.scheduleAtFixedRate(
            { cleanupExpiredTokens() },
            1,
            1,
            TimeUnit.HOURS
        )
        logger.info("TokenBlacklistService initialized with automatic cleanup")
    }
    
    fun blacklistToken(token: String, expirationTime: Long) {
        blacklistedTokens[token] = expirationTime
        logger.info("Token blacklisted: ${maskToken(token)} (expires at: $expirationTime)")
    }
    
    fun isTokenBlacklisted(token: String): Boolean {
        val expirationTime = blacklistedTokens[token]
        
        if (expirationTime == null) {
            return false
        }
        
        if (System.currentTimeMillis() >= expirationTime) {
            blacklistedTokens.remove(token)
            logger.debug("Expired blacklisted token removed: ${maskToken(token)}")
            return false
        }
        
        logger.warn("Attempt to use blacklisted token: ${maskToken(token)}")
        return true
    }
    
    private fun cleanupExpiredTokens() {
        val currentTime = System.currentTimeMillis()
        val beforeSize = blacklistedTokens.size
        
        blacklistedTokens.entries.removeIf { entry ->
            entry.value <= currentTime
        }
        
        val removed = beforeSize - blacklistedTokens.size
        if (removed > 0) {
            logger.info("Cleanup: Removed $removed expired tokens from blacklist")
        }
    }
    
    fun getBlacklistSize(): Int = blacklistedTokens.size

    fun clearBlacklist() {
        blacklistedTokens.clear()
        logger.info("Token blacklist cleared")
    }

    private fun maskToken(token: String): String {
        return if (token.length > 10) {
            "${token.substring(0, 10)}...***"
        } else {
            "***"
        }
    }
}
