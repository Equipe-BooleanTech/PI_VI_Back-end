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
class ListVaccinesUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, petId: UUID?, validOnly: Boolean): List<VaccineResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val vaccines = if (petId != null) {
            
            val pet = petRepository.findById(petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

            
            if (user.userType == UserType.OWNER && pet.ownerId != userId) {
                throw IllegalArgumentException("Pet não pertence ao usuário")
            }

            vaccineRepository.findByPetIdOrderByVaccinationDateDesc(petId)
        } else {
            
            when (user.userType) {
                UserType.VETERINARY -> vaccineRepository.findByVeterinarianIdOrderByVaccinationDateDesc(userId)
                UserType.OWNER -> {
                    
                    val ownerPets = petRepository.findByOwnerId(userId)
                    val petIds = ownerPets.map { it.id }
                    petIds.flatMap { vaccineRepository.findByPetIdOrderByVaccinationDateDesc(it) }
                }
                else -> emptyList()
            }
        }

        return vaccines.map { it.toVaccineResponse() }
    }
}
