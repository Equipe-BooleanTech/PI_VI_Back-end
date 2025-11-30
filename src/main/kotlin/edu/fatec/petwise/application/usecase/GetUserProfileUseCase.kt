package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.entity.User
import edu.fatec.petwise.domain.repository.TokenBlacklistRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Service
class GetUserProfileUseCase(
    private val userRepository: UserRepository,
    private val tokenBlacklistRepository: TokenBlacklistRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID): Optional<User> {
        logger.info("Buscando perfil do usuário: $userId")
        return userRepository.findById(userId)
    }

    
    @Transactional
    fun clearUserBlacklist(userId: UUID): Int {
        logger.info("Limpando blacklist de tokens para usuário: $userId")
        val deletedCount = tokenBlacklistRepository.deleteByUserId(userId)
        logger.info("Removidos $deletedCount tokens da blacklist do usuário $userId")
        return deletedCount
    }
}