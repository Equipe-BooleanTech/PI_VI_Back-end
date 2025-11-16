package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetListResponse
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetFavoritePetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, page: Int, pageSize: Int): PetListResponse {
        val pets = petRepository.findFavoritesByOwnerId(userId)

        logger.info("Listados ${pets.size} pets favoritos")

        return PetListResponse(
            pets = pets.map { PetResponse.fromEntity(it) },
            total = pets.size,
            page = 1,
            pageSize = pets.size
        )
    }
}
