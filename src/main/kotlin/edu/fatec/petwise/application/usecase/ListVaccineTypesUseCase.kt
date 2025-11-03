package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.VaccineTypeResponse
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.VaccineTypeRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.*

@Service
class ListVaccineTypesUseCase(
    private val vaccineTypeRepository: VaccineTypeRepository,
    private val petRepository: PetRepository
) {
    fun execute(authentication: Authentication, species: String?): List<VaccineTypeResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        
        val vaccineTypes = if (species != null) {
            // Verificar se o usuário tem pets desta espécie
            val petsOfSpecies = petRepository.findByOwnerId(userId)
                .filter { it.especie.equals(species, ignoreCase = true) }
            
            if (petsOfSpecies.isEmpty()) {
                throw IllegalArgumentException("Usuário não possui pets da espécie: $species")
            }
            
            vaccineTypeRepository.findBySpeciesAndActiveTrueOrderByVaccineName(species)
        } else {
            vaccineTypeRepository.findByActiveTrueOrderBySpeciesVaccineName()
        }
        
        return vaccineTypes.map { it.toVaccineTypeResponse() }
    }
}