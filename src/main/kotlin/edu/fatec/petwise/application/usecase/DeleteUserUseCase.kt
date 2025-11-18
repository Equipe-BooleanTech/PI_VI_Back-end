package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID): MessageResponse {
        logger.info("Deletando usuário: $userId")

        val user = userRepository.findById(userId).orElseThrow { Exception("Usuário não encontrado") }

        userRepository.deleteById(userId)
        logger.info("Usuário $userId deletado com sucesso")

        return MessageResponse("Usuário deletado com sucesso")
    }
}