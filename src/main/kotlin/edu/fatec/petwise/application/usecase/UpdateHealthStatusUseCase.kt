package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdateHealthStatusRequest
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class UpdateHealthStatusUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(petId: UUID, request: UpdateHealthStatusRequest, authentication: Authentication): PetResponse {
        val userId = UUID.fromString(authentication.name)
        
        val pet = petRepository.findById(petId).orElseThrow { Exception("Pet não encontrado") }

        
        if (pet.ownerId != userId) {
            throw Exception("Usuário não tem permissão para atualizar este pet")
        }

        
        val healthStatus = try {
            HealthStatus.valueOf(request.healthStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Status de saúde inválido: ${request.healthStatus}")
        }

        pet.healthStatus = healthStatus
        pet.healthHistory = request.notes ?: pet.healthHistory
        pet.updatedAt = LocalDateTime.now()

        val savedPet = petRepository.save(pet)

        logger.info("Status de saúde do pet ${pet.name} atualizado para ${savedPet.healthStatus}")

        return PetResponse.fromEntity(savedPet)
    }
}
