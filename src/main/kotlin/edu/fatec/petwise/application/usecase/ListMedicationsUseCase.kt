package edu.fatec.petwise.application.usecase

import com.petwise.dto.MedicationResponse
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListMedicationsUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository
) {
    fun execute(authentication: Authentication, petId: UUID?, administered: Boolean?): List<MedicationResponse> {
        val userId = UUID.fromString(authentication.principal.toString())

        val medications = when {
            // Caso: filtro por pet + administered
            petId != null && administered != null -> {
                val pet = petRepository.findByIdAndOwnerId(petId, userId)
                    ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")

                medicationRepository.findByPetIdAndAdministered(pet.id, administered)
            }

            // Caso: filtro por pet apenas
            petId != null -> {
                val pet = petRepository.findByIdAndOwnerId(petId, userId)
                    ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")

                medicationRepository.findByPetIdAndActiveTrueOrderByCreatedAtDesc(pet.id)
            }

            // Caso: filtro apenas por administered
            administered != null -> {
                medicationRepository.findByUserIdAndActiveTrue(userId)
                    .filter { it.administered == administered }
            }

            // Caso: listar tudo do usuário
            else -> {
                medicationRepository.findByUserIdAndActiveTrue(userId)
            }
        }

        return medications.map { it.toMedicationResponse() }
    }
}
