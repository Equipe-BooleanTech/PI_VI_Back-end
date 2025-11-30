package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ToggleFavoriteResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ToggleFavoriteUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, petId: UUID): ToggleFavoriteResponse {
        val pet = petRepository.findById(petId).orElseThrow { Exception("Pet não encontrado") }

        
        pet.isFavorite = !pet.isFavorite
        pet.updatedAt = LocalDateTime.now()

        val savedPet = petRepository.save(pet)

        logger.info("Favorito do pet ${pet.name} alterado para ${savedPet.isFavorite}")

        return ToggleFavoriteResponse(
            petId = petId,
            isFavorite = savedPet.isFavorite
        )
    }
}
