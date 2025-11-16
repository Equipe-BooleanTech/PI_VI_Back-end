package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MedicationRequest
import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.domain.repository.MedicationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class UpdateMedicationUseCase(
    private val medicationRepository: MedicationRepository
) {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun execute(id: UUID, request: MedicationRequest): MedicationResponse {
        val existingMedication = medicationRepository.findById(id).orElseThrow { IllegalArgumentException("Medicação não encontrada") }

        if (request.endDate != null && request.startDate != null && request.endDate.isBefore(request.startDate)) {
            throw IllegalArgumentException("Data final deve ser posterior à data inicial")
        }

        existingMedication.prescriptionId = request.prescriptionId
        existingMedication.medicationName = request.medicationName
        existingMedication.dosage = request.dosage
        existingMedication.frequency = request.frequency
        existingMedication.durationDays = request.durationDays
        existingMedication.startDate = request.startDate
        existingMedication.endDate = request.endDate
        existingMedication.sideEffects = request.sideEffects ?: existingMedication.sideEffects
        existingMedication.updatedAt = LocalDateTime.now()

        val savedMedication = medicationRepository.save(existingMedication)
        return MedicationResponse.fromEntity(savedMedication)
    }
}
