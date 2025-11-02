package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.UpdateProfileDto
import edu.fatec.petwise.application.dto.UserResponse
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UpdateUserProfileUseCase(
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, request: UpdateProfileDto): UserResponse {
        val user = userRepository.findById(java.util.UUID.fromString(userId))
            ?: throw IllegalArgumentException("Usuário não encontrado")

        if (request.fullName != null) user.fullName = request.fullName
        if (request.phone != null) user.phone = user.phone.copy(value = request.phone)

        val updated = userRepository.save(user)
        logger.info("Perfil atualizado para usuário $userId")
        return updated.toResponse()
    }
}
