package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetGender
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class UpdatePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, petId: UUID, request: UpdatePetRequest): PetResponse {
        val pet = petRepository.findById(petId).orElseThrow { Exception("Pet não encontrado") }

        // Check if user is the owner
        if (pet.ownerId != userId) {
            throw Exception("Você não tem permissão para atualizar este pet")
        }

        // Update pet
        request.name?.let { pet.name = it.trim() }
        request.breed?.let { pet.breed = it.trim() }
        request.species?.let { pet.species = PetSpecies.valueOf(it.uppercase()) }
        request.gender?.let { pet.gender = PetGender.valueOf(it.uppercase()) }
        request.age?.let { pet.age = it }
        request.weight?.let { pet.weight = it }
        request.healthStatus?.let { pet.healthStatus = HealthStatus.valueOf(it.uppercase()) }
        request.ownerName?.let { pet.ownerName = it.trim() }
        request.ownerPhone?.let { pet.ownerPhone = it.trim() }
        request.healthHistory?.let { pet.healthHistory = it.trim() }
        request.profileImageUrl?.let { pet.profileImageUrl = it.trim() }
        request.isFavorite?.let { pet.isFavorite = it }
        request.nextAppointment?.let { pet.nextAppointment = it }
        pet.updatedAt = LocalDateTime.now()

        val savedPet = petRepository.save(pet)

        logger.info("Pet $petId atualizado com sucesso pelo usuário $userId")

        return PetResponse.fromEntity(savedPet)
    }
}
