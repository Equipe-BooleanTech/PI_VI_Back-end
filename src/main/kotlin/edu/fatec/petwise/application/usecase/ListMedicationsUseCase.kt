package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.domain.entity.MedicationFilterOptions
import edu.fatec.petwise.domain.repository.MedicationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListMedicationsUseCase(
    private val medicationRepository: MedicationRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, petId: UUID?, searchQuery: String?): List<MedicationResponse> {
        val filterOptions = MedicationFilterOptions(
            petId = petId,
            searchQuery = searchQuery ?: ""
        )

        val medications = medicationRepository.filterMedications(filterOptions)

        return medications.map { MedicationResponse.fromEntity(it) }
    }
}
