package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeletePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, petId: UUID): MessageResponse {
        val pet = petRepository.findById(petId).orElseThrow { Exception("Pet não encontrado") }

        if (pet.ownerId != userId) {
            throw Exception("Você não tem permissão para remover este pet")
        }

        petRepository.deleteById(petId)
        logger.info("Pet $petId removido pelo usuário $userId")
        return MessageResponse("Pet removido com sucesso")
    }
}
