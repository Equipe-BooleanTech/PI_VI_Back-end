package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MedicationRequest
import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.domain.entity.Medication
import edu.fatec.petwise.domain.enums.MedicationStatus
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class CreateMedicationUseCase(
    private val medicationRepository: MedicationRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun execute(request: MedicationRequest, authentication: Authentication): MedicationResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PHARMACY) {
            throw IllegalArgumentException("Apenas farmácias podem criar medicações")
        }

        val prescription = prescriptionRepository.findById(request.prescriptionId)
            ?: throw IllegalArgumentException("Prescrição não encontrada")

        val existingMedication = medicationRepository
            .findByPrescriptionId(request.prescriptionId)
            .firstOrNull { it.medicationName.equals(request.medicationName, ignoreCase = true) }

        if (existingMedication != null) {
            throw IllegalArgumentException("Medicação já cadastrada para esta prescrição")
        }

        val now = LocalDateTime.now()
        val medication = Medication(
            id = UUID.randomUUID(),
            userId = userId,
            prescriptionId = request.prescriptionId,
            medicationName = request.medicationName,
            dosage = request.dosage,
            frequency = request.frequency,
            durationDays = request.durationDays,
            startDate = request.startDate,
            endDate = request.endDate,
            sideEffects = request.sideEffects ?: "",
            status = MedicationStatus.ACTIVE,
            createdAt = now,
            updatedAt = now
        )

        val savedMedication = medicationRepository.save(medication)

        logger.info("Medicação criada: ID=${savedMedication.id}, Prescription=${request.prescriptionId}")

        return MedicationResponse.fromEntity(savedMedication)
    }
}
