package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListUserPetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, includeInactive: Boolean = false): List<PetResponse> {
        val ownerId = UUID.fromString(userId)
        
        val pets = if (includeInactive) {
            petRepository.findByOwnerId(ownerId)
        } else {
            petRepository.findByOwnerIdAndAtivoTrue(ownerId)
        }
        
        logger.info("Listados ${pets.size} pets para o usuário $userId")
        
        return pets.map { PetResponse.fromEntity(it) }
    }
}
