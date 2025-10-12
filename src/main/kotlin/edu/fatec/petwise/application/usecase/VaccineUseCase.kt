package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreateVaccineRequest
import edu.fatec.petwise.application.dto.UpdateVaccineRequest
import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.VaccineRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateVaccineUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: CreateVaccineRequest, ownerId: UUID): VaccineResponse {
        logger.info("Criando vacina para pet: ${request.petId}")

        val petId = UUID.fromString(request.petId)
        val veterinaryId = UUID.fromString(request.veterinaryId)

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.tutorId != ownerId) {
            logger.warn("Tutor $ownerId tentou criar vacina para pet de outro tutor")
            throw BusinessRuleException("Você não tem permissão para adicionar vacinas a este pet")
        }

        if (!userRepository.existsById(veterinaryId)) {
            throw EntityNotFoundException("Veterinário", veterinaryId)
        }

        val vaccine = Vaccine(
            petId = petId,
            name = request.name,
            manufacturer = request.manufacturer,
            batchNumber = request.batchNumber,
            applicationDate = request.applicationDate,
            nextDoseDate = request.nextDoseDate,
            veterinaryId = veterinaryId,
            notes = request.notes
        )

        val saved = vaccineRepository.save(vaccine)
        logger.info("Vacina criada com sucesso. ID: ${saved.id}")

        return saved.toResponse()
    }
}

@Service
class GetVaccineByIdUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID): VaccineResponse {
        logger.info("Buscando vacina por ID: $id")

        val vaccine = vaccineRepository.findById(id)
            ?: throw EntityNotFoundException("Vacina", id)

        val pet = petRepository.findById(vaccine.petId)
            ?: throw EntityNotFoundException("Pet", vaccine.petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar esta vacina")
        }

        return vaccine.toResponse()
    }
}

@Service
class GetVaccinesByPetUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(petId: UUID, ownerId: UUID): List<VaccineResponse> {
        logger.info("Buscando vacinas do pet: $petId")

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar vacinas deste pet")
        }

        return vaccineRepository.findByPetId(petId).map { it.toResponse() }
    }
}

@Service
class GetDueVaccinesByPetUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(petId: UUID, ownerId: UUID): List<VaccineResponse> {
        logger.info("Buscando vacinas pendentes do pet: $petId")

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar vacinas deste pet")
        }

        return vaccineRepository.findDueVaccinesByPetId(petId).map { it.toResponse() }
    }
}

@Service
class UpdateVaccineUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, request: UpdateVaccineRequest, ownerId: UUID): VaccineResponse {
        logger.info("Atualizando vacina ID: $id")

        val vaccine = vaccineRepository.findById(id)
            ?: throw EntityNotFoundException("Vacina", id)

        val pet = petRepository.findById(vaccine.petId)
            ?: throw EntityNotFoundException("Pet", vaccine.petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para atualizar esta vacina")
        }

        val updated = vaccine.copy(
            nextDoseDate = request.nextDoseDate ?: vaccine.nextDoseDate,
            notes = request.notes ?: vaccine.notes,
            updatedAt = java.time.LocalDateTime.now()
        )

        val saved = vaccineRepository.update(updated)
        logger.info("Vacina atualizada com sucesso. ID: $id")

        return saved.toResponse()
    }
}

@Service
class DeleteVaccineUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID) {
        logger.info("Excluindo vacina ID: $id")

        val vaccine = vaccineRepository.findById(id)
            ?: throw EntityNotFoundException("Vacina", id)

        val pet = petRepository.findById(vaccine.petId)
            ?: throw EntityNotFoundException("Pet", vaccine.petId)

        if (pet.tutorId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para excluir esta vacina")
        }

        vaccineRepository.delete(id)
        logger.info("Vacina excluída com sucesso. ID: $id")
    }
}

private fun Vaccine.toResponse() = VaccineResponse(
    id = this.id.toString(),
    petId = this.petId.toString(),
    name = this.name,
    manufacturer = this.manufacturer,
    batchNumber = this.batchNumber,
    applicationDate = this.applicationDate.toString(),
    nextDoseDate = this.nextDoseDate?.toString(),
    veterinaryId = this.veterinaryId.toString(),
    notes = this.notes,
    isDueForNextDose = this.isDueForNextDose(),
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
