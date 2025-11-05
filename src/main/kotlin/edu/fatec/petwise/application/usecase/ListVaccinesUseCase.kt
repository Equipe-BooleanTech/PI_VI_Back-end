package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.VaccineRepository
import edu.fatec.petwise.domain.repository.VaccineTypeRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDate
import java.util.UUID

@Service
class ListVaccinesUseCase(
    private val vaccineRepository: VaccineRepository,
    private val vaccineTypeRepository: VaccineTypeRepository,
    private val petRepository: PetRepository
) {
    fun execute(authentication: Authentication, petId: UUID?, validOnly: Boolean): List<VaccineResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        
        val vaccines = if (petId != null) {
            // Verificar se o pet pertence ao usuário
            val pet = petRepository.findByIdAndOwnerId(petId, userId)
                ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")
            
            if (validOnly) {
                vaccineRepository.findValidByPetId(petId, LocalDate.now())
            } else {
                vaccineRepository.findByPetIdAndActiveTrueOrderByVaccinationDateDesc(petId)
            }
        } else {
            vaccineRepository.findByUserIdAndActiveTrue(userId)
        }
        
        return vaccines.map { it.toVaccineResponse() }
    }
}