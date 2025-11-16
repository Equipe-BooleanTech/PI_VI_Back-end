package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetListResponse
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GetAllPetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(page: Int, pageSize: Int): PetListResponse {
        val pets = petRepository.findAll()

        logger.info("Listados ${pets.size} pets")

        return PetListResponse(
            pets = pets.map { PetResponse.fromEntity(it) },
            total = pets.size,
            page = 1,
            pageSize = pets.size
        )
    }
}
