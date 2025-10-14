package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.CompleteAppointmentRequest
import edu.fatec.petwise.application.dto.CreateAppointmentRequest
import edu.fatec.petwise.application.dto.UpdateAppointmentRequest
import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: CreateAppointmentRequest, ownerId: UUID): AppointmentResponse {
        logger.info("Criando consulta para pet: ${request.petId}")

        val petId = UUID.fromString(request.petId)
        val veterinaryId = UUID.fromString(request.veterinaryId)

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.ownerId != ownerId) {
            logger.warn("Tutor $ownerId tentou criar consulta para pet de outro tutor")
            throw BusinessRuleException("Você não tem permissão para agendar consultas para este pet")
        }

        if (!userRepository.existsById(veterinaryId)) {
            throw EntityNotFoundException("Veterinário", veterinaryId)
        }

        val appointment = Appointment(
            petId = petId,
            veterinaryId = veterinaryId,
            ownerId = ownerId,
            scheduledDate = request.scheduledDate,
            reason = request.reason,
            notes = request.notes
        )

        val saved = appointmentRepository.save(appointment)
        logger.info("Consulta criada com sucesso. ID: ${saved.id}")

        return saved.toResponse()
    }
}

@Service
class GetAppointmentByIdUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, userId: UUID): AppointmentResponse {
        logger.info("Buscando consulta por ID: $id")

        val appointment = appointmentRepository.findById(id)
            ?: throw EntityNotFoundException("Consulta", id)

        if (appointment.ownerId != userId) {
            throw BusinessRuleException("Você não tem permissão para visualizar esta consulta")
        }

        return appointment.toResponse()
    }
}

@Service
class GetAppointmentsByOwnerUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: UUID): List<AppointmentResponse> {
        logger.info("Buscando consultas do tutor: $ownerId")
        return appointmentRepository.findByOwnerId(ownerId).map { it.toResponse() }
    }
}

@Service
class GetAppointmentsByPetUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(petId: UUID, ownerId: UUID): List<AppointmentResponse> {
        logger.info("Buscando consultas do pet: $petId")

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar consultas deste pet")
        }

        return appointmentRepository.findByPetId(petId).map { it.toResponse() }
    }
}

@Service
class UpdateAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, request: UpdateAppointmentRequest, ownerId: UUID): AppointmentResponse {
        logger.info("Atualizando consulta ID: $id")

        val appointment = appointmentRepository.findById(id)
            ?: throw EntityNotFoundException("Consulta", id)

        if (appointment.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para atualizar esta consulta")
        }

        if (!appointment.canBeModified()) {
            throw BusinessRuleException("Esta consulta não pode ser modificada no status atual")
        }

        val updated = appointment.copy(
            scheduledDate = request.scheduledDate ?: appointment.scheduledDate,
            reason = request.reason ?: appointment.reason,
            notes = request.notes ?: appointment.notes,
            updatedAt = java.time.LocalDateTime.now()
        )

        val saved = appointmentRepository.update(updated)
        logger.info("Consulta atualizada com sucesso. ID: $id")

        return saved.toResponse()
    }
}

@Service
class CancelAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID): AppointmentResponse {
        logger.info("Cancelando consulta ID: $id")

        val appointment = appointmentRepository.findById(id)
            ?: throw EntityNotFoundException("Consulta", id)

        if (appointment.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para cancelar esta consulta")
        }

        if (!appointment.canBeCancelled()) {
            throw BusinessRuleException("Esta consulta não pode ser cancelada")
        }

        val cancelled = appointment.cancel()
        val saved = appointmentRepository.update(cancelled)

        logger.info("Consulta cancelada com sucesso. ID: $id")
        return saved.toResponse()
    }
}

private fun Appointment.toResponse() = AppointmentResponse(
    id = this.id.toString(),
    petId = this.petId.toString(),
    veterinaryId = this.veterinaryId.toString(),
    ownerId = this.ownerId.toString(),
    scheduledDate = this.scheduledDate.toString(),
    reason = this.reason,
    notes = this.notes,
    diagnosis = this.diagnosis,
    treatment = this.treatment,
    status = this.status.name,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
