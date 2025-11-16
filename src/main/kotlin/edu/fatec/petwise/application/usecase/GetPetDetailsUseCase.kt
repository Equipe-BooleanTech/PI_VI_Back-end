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

    fun execute(petId: UUID): PetResponse {
        val pet = petRepository.findById(petId).orElseThrow { Exception("Pet não encontrado") }

        logger.info("Detalhes do pet $petId obtidos")

        return PetResponse.fromEntity(pet)
    }
}
