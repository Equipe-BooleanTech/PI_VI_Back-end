package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreateMedicationRequest
import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.application.dto.UpdateMedicationRequest
import edu.fatec.petwise.domain.entity.Medication
import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateMedicationUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: CreateMedicationRequest, ownerId: UUID): MedicationResponse {
        logger.info("Criando medicação para pet: ${request.petId}")

        val petId = UUID.fromString(request.petId)
        val prescribedBy = UUID.fromString(request.prescribedBy)

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.tutorId != ownerId) {
            logger.warn("Tutor $ownerId tentou criar medicação para pet de outro tutor")
            throw BusinessRuleException("Você não tem permissão para adicionar medicações a este pet")
        }

        if (!userRepository.existsById(prescribedBy)) {
            throw EntityNotFoundException("Veterinário", prescribedBy)
        }

        val medication = Medication(
            petId = petId,
            name = request.name,
            dosage = request.dosage,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            prescribedBy = prescribedBy,
            instructions = request.instructions
        )

        val saved = medicationRepository.save(medication)
        logger.info("Medicação criada com sucesso. ID: ${saved.id}")

        return saved.toResponse()
    }
}

@Service
class GetMedicationByIdUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID): MedicationResponse {
        logger.info("Buscando medicação por ID: $id")

        val medication = medicationRepository.findById(id)
            ?: throw EntityNotFoundException("Medicação", id)

        val pet = petRepository.findById(medication.petId)
            ?: throw EntityNotFoundException("Pet", medication.petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar esta medicação")
        }

        return medication.toResponse()
    }
}

@Service
class GetMedicationsByPetUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(petId: UUID, ownerId: UUID): List<MedicationResponse> {
        logger.info("Buscando medicações do pet: $petId")

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar medicações deste pet")
        }

        return medicationRepository.findByPetId(petId).map { it.toResponse() }
    }
}

@Service
class GetActiveMedicationsByPetUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(petId: UUID, ownerId: UUID): List<MedicationResponse> {
        logger.info("Buscando medicações ativas do pet: $petId")

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar medicações deste pet")
        }

        return medicationRepository.findActiveMedicationsByPetId(petId).map { it.toResponse() }
    }
}

@Service
class UpdateMedicationUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, request: UpdateMedicationRequest, ownerId: UUID): MedicationResponse {
        logger.info("Atualizando medicação ID: $id")

        val medication = medicationRepository.findById(id)
            ?: throw EntityNotFoundException("Medicação", id)

        val pet = petRepository.findById(medication.petId)
            ?: throw EntityNotFoundException("Pet", medication.petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para atualizar esta medicação")
        }

        val updated = medication.copy(
            dosage = request.dosage ?: medication.dosage,
            frequency = request.frequency ?: medication.frequency,
            instructions = request.instructions ?: medication.instructions,
            updatedAt = java.time.LocalDateTime.now()
        )

        val saved = medicationRepository.update(updated)
        logger.info("Medicação atualizada com sucesso. ID: $id")

        return saved.toResponse()
    }
}

@Service
class DeactivateMedicationUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID): MedicationResponse {
        logger.info("Desativando medicação ID: $id")

        val medication = medicationRepository.findById(id)
            ?: throw EntityNotFoundException("Medicação", id)

        val pet = petRepository.findById(medication.petId)
            ?: throw EntityNotFoundException("Pet", medication.petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para desativar esta medicação")
        }

        val deactivated = medication.deactivate()
        val saved = medicationRepository.update(deactivated)

        logger.info("Medicação desativada com sucesso. ID: $id")
        return saved.toResponse()
    }
}

private fun Medication.toResponse() = MedicationResponse(
    id = this.id.toString(),
    petId = this.petId.toString(),
    name = this.name,
    dosage = this.dosage,
    frequency = this.frequency,
    startDate = this.startDate.toString(),
    endDate = this.endDate.toString(),
    prescribedBy = this.prescribedBy.toString(),
    instructions = this.instructions,
    active = this.active,
    isExpired = this.isExpired(),
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
