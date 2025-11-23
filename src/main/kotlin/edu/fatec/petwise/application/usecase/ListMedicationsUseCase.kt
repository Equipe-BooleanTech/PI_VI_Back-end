package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.domain.entity.MedicationFilterOptions
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class ListMedicationsUseCase(
    private val medicationRepository: MedicationRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(authentication: Authentication, petId: UUID?, searchQuery: String?): List<MedicationResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PHARMACY) {
            throw IllegalArgumentException("Apenas farmácias podem listar medicações")
        }

        val filterOptions = MedicationFilterOptions(
            petId = petId,
            searchQuery = searchQuery ?: ""
        )

        val medications = medicationRepository.filterMedications(filterOptions)

        return medications.map { MedicationResponse.fromEntity(it) }
    }
}
