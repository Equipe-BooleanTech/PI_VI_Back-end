package edu.fatec.petwise.infrastructure.service

import edu.fatec.petwise.domain.repository.TokenBlacklistRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TokenCleanupService(
    private val tokenBlacklistRepository: TokenBlacklistRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    
    @Scheduled(cron = "0 0 3 * * ?") 
    fun cleanupExpiredTokens() {
        logger.info("Iniciando limpeza de tokens expirados da blacklist")

        try {
            val now = LocalDateTime.now()
            val deletedCount = tokenBlacklistRepository.deleteByExpiresAtBefore(now)

            if (deletedCount > 0) {
                logger.info("Limpeza concluída: $deletedCount tokens expirados removidos da blacklist")
            } else {
                logger.info("Limpeza concluída: nenhum token expirado encontrado")
            }
        } catch (e: Exception) {
            logger.error("Erro durante limpeza de tokens expirados: ${e.message}", e)
        }
    }
}