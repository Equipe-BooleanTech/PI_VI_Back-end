package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetListResponse
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetAllPetsUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, page: Int, pageSize: Int): PetListResponse {
        val user = try {
            userRepository.findById(userId).orElseThrow { Exception("User not found") }
        } catch (e: Exception) {
            logger.warn("User with ID $userId not found, returning empty pet list")
            // For non-existent users, return empty list
            return PetListResponse(
                pets = emptyList(),
                total = 0,
                page = 1,
                pageSize = 0
            )
        }
        
        val pets = if (user.userType.name == "OWNER") {
            // For owners, only return their own pets
            petRepository.findByOwnerId(userId)
        } else {
            // For other user types (VETERINARY, PHARMACY, etc.), return all pets
            petRepository.findAll()
        }

        logger.info("Listados ${pets.size} pets para usuário $userId (tipo: ${user.userType})")

        return PetListResponse(
            pets = pets.map { PetResponse.fromEntity(it) },
            total = pets.size,
            page = 1,
            pageSize = pets.size
        )
    }
}
