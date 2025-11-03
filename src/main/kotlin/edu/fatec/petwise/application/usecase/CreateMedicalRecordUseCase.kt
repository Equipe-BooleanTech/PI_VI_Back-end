package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MedicalRecordRequest
import edu.fatec.petwise.application.dto.MedicalRecordResponse
import edu.fatec.petwise.domain.entity.MedicalRecord
import edu.fatec.petwise.domain.repository.MedicalRecordRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.*

@Service
class CreateMedicalRecordUseCase(
    private val medicalRecordRepository: MedicalRecordRepository,
    private val petRepository: PetRepository
) {
    fun execute(request: MedicalRecordRequest, authentication: Authentication): MedicalRecordResponse {
        val ownerId = UUID.fromString(authentication.principal.toString())

        // Verifica se o pet pertence ao usuário autenticado
        val pet = petRepository.findByIdAndOwnerId(request.petId, ownerId)
            ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")

        // Verifica se o veterinário informado é o mesmo autenticado
        if (request.veterinarian != authentication.name) {
            throw IllegalArgumentException("Veterinário não autorizado para este registro")
        }

        // Cria o registro médico
        val medicalRecord = MedicalRecord(
            id = UUID.randomUUID(),
            userId = ownerId,
            petId = request.petId,
            veterinarian = request.veterinarian,
            appointmentId = request.appointmentId,
            recordDate = request.recordDate,
            diagnosis = request.diagnosis,
            treatment = request.treatment,
            observations = request.observations,
            vitalSigns = request.vitalSigns,
            weightKg = request.weightKg,
            temperature = request.temperature
        )

        val savedRecord = medicalRecordRepository.save(medicalRecord)
        return savedRecord.toMedicalRecordResponse()
    }
}
