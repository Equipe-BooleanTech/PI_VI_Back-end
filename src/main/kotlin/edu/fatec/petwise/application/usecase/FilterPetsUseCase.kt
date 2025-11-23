package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetListResponse
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.entity.PetFilterOptions
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FilterPetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(options: PetFilterOptions): PetListResponse {
        val pets = petRepository.filterPets(options)

        logger.info("Filtrados ${pets.size} pets com opções: $options")

        return PetListResponse(
            pets = pets.map { PetResponse.fromEntity(it) },
            total = pets.size,
            page = 1,
            pageSize = pets.size
        )
    }
}
