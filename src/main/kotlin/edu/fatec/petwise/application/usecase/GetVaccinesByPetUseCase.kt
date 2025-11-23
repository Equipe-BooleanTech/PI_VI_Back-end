package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.VaccineRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class GetVaccinesByPetUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, petId: UUID): List<VaccineResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        // Verificar se o pet existe
        val pet = petRepository.findById(petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

        // If user is OWNER, check if the pet belongs to them
        if (user.userType == UserType.OWNER && pet.ownerId != userId) {
            throw IllegalArgumentException("Pet não pertence ao usuário")
        }

        val vaccines = vaccineRepository.findByPetIdOrderByVaccinationDateDesc(petId)

        return vaccines.map { it.toVaccineResponse() }
    }
}