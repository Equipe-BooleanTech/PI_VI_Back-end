package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class GetPrescriptionsByPetUseCase(
    private val prescriptionRepository: PrescriptionRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, petId: UUID): List<PrescriptionResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        // Verificar se o pet existe
        val pet = petRepository.findById(petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

        // Allow access based on user type:
        // - VETERINARY: can see prescriptions for any pet
        // - OWNER: can only see prescriptions for their own pets
        // - ADMIN/PHARMACY: can see all prescriptions
        when (user.userType) {
            UserType.OWNER -> {
                if (pet.ownerId != userId) {
                    throw IllegalArgumentException("Pet não pertence ao usuário")
                }
            }
            UserType.VETERINARY -> {
                // Veterinarians can see prescriptions for any pet
            }
            UserType.ADMIN, UserType.PHARMACY -> {
                // Admins and pharmacies can see all prescriptions
            }
            else -> {
                throw IllegalArgumentException("Tipo de usuário não autorizado")
            }
        }

        val prescriptions = prescriptionRepository.findByPetId(petId)

        return prescriptions.map { PrescriptionResponse.fromEntity(it) }
    }
}