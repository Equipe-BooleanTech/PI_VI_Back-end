package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.repository.MedicationRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeleteMedicationUseCase(
    private val medicationRepository: MedicationRepository
) {
    fun execute(id: UUID) {
        if (!medicationRepository.existsById(id)) {
            throw IllegalArgumentException("Medicação não encontrada")
        }

        medicationRepository.deleteById(id)
    }
}
