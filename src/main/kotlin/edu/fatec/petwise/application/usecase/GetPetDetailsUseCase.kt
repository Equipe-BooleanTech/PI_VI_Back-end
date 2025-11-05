package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetPetDetailsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, petId: String): PetResponse {
        val ownerId = UUID.fromString(userId)
        val petUuid = UUID.fromString(petId)
        
        val pet = petRepository.findByIdAndOwnerId(petUuid, ownerId)
            ?: throw Exception("Pet não encontrado ou você não tem permissão para acessá-lo")
        
        logger.info("Detalhes do pet $petId obtidos pelo usuário $userId")
        
        return PetResponse.fromEntity(pet)
    }
}
