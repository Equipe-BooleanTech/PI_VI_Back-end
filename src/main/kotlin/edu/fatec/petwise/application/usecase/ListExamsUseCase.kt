package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ExamResponse
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.ExamRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class ListExamsUseCase(
    private val examRepository: ExamRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, petId: UUID?): List<ExamResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val exams = if (petId != null) {
            // Verificar se o pet existe
            val pet = petRepository.findById(petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

            // If user is OWNER, check if the pet belongs to them
            if (user.userType == UserType.OWNER && pet.ownerId != userId) {
                throw IllegalArgumentException("Pet não pertence ao usuário")
            }

            examRepository.findByPetId(petId)
        } else {
            // If no petId specified, return exams based on user type
            when (user.userType) {
                UserType.VETERINARY -> examRepository.findByVeterinaryId(userId)
                UserType.OWNER -> {
                    // For owners, get all exams for their pets
                    val ownerPets = petRepository.findByOwnerId(userId)
                    val petIds = ownerPets.map { it.id }
                    petIds.flatMap { examRepository.findByPetId(it) }
                }
                else -> emptyList()
            }
        }

        return exams.map { it.toExamResponse() }
    }
}
