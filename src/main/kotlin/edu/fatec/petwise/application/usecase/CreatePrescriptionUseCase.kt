package edu.fatec.petwise.application.usecase

import com.petwise.dto.PrescriptionRequest
import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.domain.entity.Prescription
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.*
import java.time.LocalDateTime

@Service
class CreatePrescriptionUseCase(
    private val prescriptionRepository: PrescriptionRepository,
    private val petRepository: PetRepository
) {
    fun execute(request: PrescriptionRequest, authentication: Authentication): PrescriptionResponse {
        val userId = UUID.fromString(authentication.principal.toString())

        // Verifica se o pet pertence ao usuário autenticado
        val pet = petRepository.findByIdAndOwnerId(UUID.fromString(request.petId.toString()), userId)
            ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")

        // Verifica se o veterinário informado é o mesmo autenticado
        if (request.veterinarian != authentication.name) {
            throw IllegalArgumentException("Veterinário não autorizado para esta prescrição")
        }

        // Verifica se a data de validade é posterior à data da prescrição
        if (request.validUntil != null && request.validUntil.isBefore(request.prescriptionDate)) {
            throw IllegalArgumentException("Data de validade deve ser posterior à data da prescrição")
        }

        // Cria a prescrição
        val prescription = Prescription(
            id = UUID.randomUUID(),
            userId = userId,
            petId = pet.id,
            veterinarian = request.veterinarian,
            medicalRecordId = UUID.fromString(request.medicalRecordId.toString()),
            prescriptionDate = request.prescriptionDate,
            instructions = request.instructions,
            diagnosis = request.diagnosis,
            validUntil = request.validUntil,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedPrescription = prescriptionRepository.save(prescription)
        return savedPrescription.toPrescriptionResponse()
    }
}
