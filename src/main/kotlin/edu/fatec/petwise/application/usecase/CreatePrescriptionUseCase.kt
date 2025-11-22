package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PrescriptionRequest
import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.application.dto.UpdatePrescriptionRequest
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

        // Verifica se o pet existe
        val pet = petRepository.findById(request.petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

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

@Service
class GetPrescriptionByIdUseCase(
    private val prescriptionRepository: PrescriptionRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, id: UUID): PrescriptionResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val prescription = prescriptionRepository.findById(id).orElseThrow { IllegalArgumentException("Prescrição não encontrada") }

        // Verificar se o pet existe para validação de acesso
        val pet = petRepository.findById(prescription.petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

        // Allow access based on user type:
        // - VETERINARY: can see prescriptions for any pet
        // - OWNER: can only see prescriptions for their own pets
        // - ADMIN/PHARMACY: can see all prescriptions
        when (user.userType) {
            UserType.OWNER -> {
                if (pet.ownerId != userId) {
                    throw IllegalArgumentException("Pet não pertence ao usuário")
                }
            }
            UserType.VETERINARY -> {
                // Veterinarians can see prescriptions for any pet
            }
            UserType.ADMIN, UserType.PHARMACY -> {
                // Admins and pharmacies can see all prescriptions
            }
            else -> {
                throw IllegalArgumentException("Tipo de usuário não autorizado")
            }
        }

        return PrescriptionResponse.fromEntity(prescription)
    }
}

@Service
class UpdatePrescriptionUseCase(
    private val prescriptionRepository: PrescriptionRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, request: UpdatePrescriptionRequest, authentication: Authentication): PrescriptionResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem atualizar prescrições")
        }

        val existingPrescription = prescriptionRepository.findById(id).orElseThrow { IllegalArgumentException("Prescrição não encontrada") }

        // Update the prescription fields if provided
        request.instructions?.let { existingPrescription.instructions = it }
        request.diagnosis?.let { existingPrescription.diagnosis = it }
        request.validUntil?.let { existingPrescription.validUntil = it }
        request.status?.let { existingPrescription.status = it }
        request.medications?.let { existingPrescription.medications = it }
        request.observations?.let { existingPrescription.observations = it }
        request.active?.let { existingPrescription.active = it }

        existingPrescription.updatedAt = LocalDateTime.now()

        val savedPrescription = prescriptionRepository.save(existingPrescription)
        return PrescriptionResponse.fromEntity(savedPrescription)
    }
}

@Service
class DeletePrescriptionUseCase(
    private val prescriptionRepository: PrescriptionRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem excluir prescrições")
        }

        val prescription = prescriptionRepository.findById(id).orElseThrow { IllegalArgumentException("Prescrição não encontrada") }

        prescriptionRepository.deleteById(id)
    }
}
