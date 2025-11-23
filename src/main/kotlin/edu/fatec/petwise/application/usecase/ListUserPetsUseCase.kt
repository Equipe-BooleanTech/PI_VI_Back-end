package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ListPetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(includeInactive: Boolean = false): List<PetResponse> {
        val pets = petRepository.findAll()

        logger.info("Listados ${pets.size} pets")

        return pets.map { PetResponse.fromEntity(it) }
    }
}
