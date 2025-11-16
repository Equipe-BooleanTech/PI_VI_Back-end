package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PrescriptionRequest
import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.domain.entity.Prescription
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.*
import java.time.LocalDateTime
import java.util.UUID

@Service
class CreatePrescriptionUseCase(
    private val prescriptionRepository: PrescriptionRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(request: PrescriptionRequest, authentication: Authentication): PrescriptionResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem criar prescrições")
        }

        // Verifica se o pet pertence ao usuário autenticado
        val pet = petRepository.findById(request.petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

        if (pet.ownerId != userId) {
            throw IllegalArgumentException("Pet não pertence ao usuário")
        }

        // Verifica se o veterinário informado é o mesmo autenticado
        if (request.veterinarian != userId) {
            throw IllegalArgumentException("Veterinário não autorizado para esta prescrição")
        }

        // Verifica se a data de validade é posterior à data da prescrição
        if (request.validUntil != null && request.validUntil.isBefore(request.prescriptionDate)) {
            throw IllegalArgumentException("Data de validade deve ser posterior à data da prescrição")
        }

        // Cria a prescrição
        val prescription = Prescription(
            id = null,
            userId = userId,
            petId = request.petId,
            veterinaryId = request.veterinarian,
            medicalRecordId = request.medicalRecordId,
            prescriptionDate = request.prescriptionDate,
            instructions = request.instructions,
            diagnosis = request.diagnosis,
            validUntil = request.validUntil,
            status = Prescription.PrescriptionStatus.ATIVA.name,
            medications = request.medications ?: "",
            observations = request.observations ?: "",
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedPrescription = prescriptionRepository.save(prescription)
        return PrescriptionResponse.fromEntity(savedPrescription)
    }
}
