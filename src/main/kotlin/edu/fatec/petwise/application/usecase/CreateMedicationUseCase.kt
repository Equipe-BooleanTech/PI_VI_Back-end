package edu.fatec.petwise.application.usecase

import com.petwise.dto.MedicationResponse
import edu.fatec.petwise.application.dto.MedicationRequest
import edu.fatec.petwise.domain.entity.Medication
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.*

@Service
class CreateMedicationUseCase(
    private val medicationRepository: MedicationRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val petRepository: PetRepository
) {
    fun execute(request: MedicationRequest, authentication: Authentication): MedicationResponse {
        val ownerId = UUID.fromString(authentication.principal.toString())

        // Verifica se a prescrição existe e pertence ao usuário autenticado
        val prescription = prescriptionRepository.findByIdAndUserIdAndActiveTrue(request.prescriptionId, ownerId)
            ?: throw IllegalArgumentException("Prescrição não encontrada ou não pertence ao usuário")

        // Verifica se o pet da prescrição pertence ao usuário
        val pet = petRepository.findByIdAndOwnerId(prescription.petId, ownerId)
            ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")

        // Verifica se já existe uma medicação ativa com o mesmo nome para este pet
        val existingMedication = medicationRepository
            .findByPetIdAndMedicationNameContaining(pet.id, request.medicationName)
            .firstOrNull { it.medicationName.equals(request.medicationName, ignoreCase = true) && it.active }

        if (existingMedication != null) {
            throw IllegalArgumentException("Medicação já cadastrada para este pet")
        }

        // Valida datas
        if (request.endDate != null && request.startDate != null && request.endDate.isBefore(request.startDate)) {
            throw IllegalArgumentException("Data final deve ser posterior à data inicial")
        }

        // Cria o registro da medicação
        val medication = Medication(
            id = UUID.randomUUID(),
            userId = ownerId,
            petId = pet.id,
            prescriptionId = request.prescriptionId,
            medicationName = request.medicationName,
            dosage = request.dosage,
            frequency = request.frequency,
            durationDays = request.durationDays,
            startDate = request.startDate,
            endDate = request.endDate,
            administrationNotes = request.administrationNotes,
            sideEffects = request.sideEffects,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedMedication = medicationRepository.save(medication)
        return savedMedication.toMedicationResponse()
    }
}
