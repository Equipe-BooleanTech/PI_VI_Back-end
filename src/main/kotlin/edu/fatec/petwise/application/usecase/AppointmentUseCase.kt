package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.CreateAppointmentRequest
import edu.fatec.petwise.application.dto.UpdateAppointmentRequest
import edu.fatec.petwise.domain.entity.Appointment
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

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.ownerId != ownerId) {
            logger.warn("Tutor $ownerId tentou criar consulta para pet de outro tutor")
            throw BusinessRuleException("Você não tem permissão para agendar consultas para este pet")
        }

        val appointment = Appointment(
            petId = petId,
            ownerId = ownerId,
            consultaType = request.consultaType,
            consultaDate = request.consultaDate,
            consultaTime = request.consultaTime,
            symptoms = request.symptoms,
            notes = request.notes,
            price = request.price
        )

        val saved = appointmentRepository.save(appointment)
        logger.info("Consulta criada com sucesso. ID: ${saved.id}")

        val owner = userRepository.findById(ownerId)!!
        
        return saved.toResponse(
            petName = pet.name,
            veterinarianName = "",
            ownerName = owner.fullName,
            ownerPhone = owner.phone.value,
        )
    }
}

@Service
class GetAppointmentByIdUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, userId: UUID): AppointmentResponse {
        logger.info("Buscando consulta por ID: $id")

        val appointment = appointmentRepository.findById(id)
            ?: throw EntityNotFoundException("Consulta", id)

        if (appointment.ownerId != userId) {
            throw BusinessRuleException("Você não tem permissão para visualizar esta consulta")
        }

        val pet = petRepository.findById(appointment.petId)!!
        val owner = userRepository.findById(appointment.ownerId)!!

        return appointment.toResponse(
            petName = pet.name,
            veterinarianName = "",
            ownerName = owner.fullName,
            ownerPhone = owner.phone?.value,
        )
    }
}

@Service
class GetAppointmentsByOwnerUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: UUID): List<AppointmentResponse> {
        logger.info("Buscando consultas do owner: $ownerId")
        val owner = userRepository.findById(ownerId)!!
        
        return appointmentRepository.findByOwnerId(ownerId).map { appointment ->
            val pet = petRepository.findById(appointment.petId)!!
            
            appointment.toResponse(
                petName = pet.name,
                veterinarianName = "",
                ownerName = owner.fullName,
                ownerPhone = owner.phone?.value ?: ""
            )
        }
    }
}

@Service
class GetAppointmentsByPetUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(petId: UUID, ownerId: UUID): List<AppointmentResponse> {
        logger.info("Buscando consultas do pet: $petId")

        val pet = petRepository.findById(petId)
            ?: throw EntityNotFoundException("Pet", petId)

        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar consultas deste pet")
        }

        val owner = userRepository.findById(ownerId)!!

        return appointmentRepository.findByPetId(petId).map { appointment ->
            
            appointment.toResponse(
                petName = pet.name,
                veterinarianName = "",
                ownerName = owner.fullName,
                ownerPhone = owner.phone?.value ?: ""
            )
        }
    }
}

@Service
class UpdateAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
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
            consultaDate = request.consultaDate ?: appointment.consultaDate,
            consultaTime = request.consultaTime ?: appointment.consultaTime,
            symptoms = request.symptoms ?: appointment.symptoms,
            notes = request.notes ?: appointment.notes,
            price = request.price ?: appointment.price,
            updatedAt = java.time.LocalDateTime.now()
        )

        val saved = appointmentRepository.save(updated)
        logger.info("Consulta atualizada com sucesso. ID: $id")

        val pet = petRepository.findById(appointment.petId)!!
        val owner = userRepository.findById(ownerId)!!

        return saved.toResponse(
            petName = pet.name,
            veterinarianName = "",
            ownerName = owner.fullName,
            ownerPhone = owner.phone?.value ?: ""
        )
    }
}

@Service
class CancelAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
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
        val saved = appointmentRepository.save(cancelled)

        logger.info("Consulta cancelada com sucesso. ID: $id")
        
        val pet = petRepository.findById(appointment.petId)!!
        val owner = userRepository.findById(ownerId)!!

        return saved.toResponse(
            petName = pet.name,
            veterinarianName = "",
            ownerName = owner.fullName,
            ownerPhone = owner.phone?.value ?: ""
        )
    }
}

private fun Appointment.toResponse(
    petName: String,
    veterinarianName: String,
    ownerName: String,
    ownerPhone: String?
) = AppointmentResponse(
    id = this.id.toString(),
    petId = this.petId.toString(),
    petName = petName,
    veterinarianName = veterinarianName,
    consultaType = this.consultaType.displayName,
    consultaDate = this.consultaDate,
    consultaTime = this.consultaTime,
    status = this.status.displayName,
    symptoms = this.symptoms,
    diagnosis = this.diagnosis,
    treatment = this.treatment,
    prescriptions = this.prescriptions,
    notes = this.notes,
    nextAppointment = this.nextAppointment,
    price = this.price,
    isPaid = this.isPaid,
    ownerName = ownerName,
    ownerPhone = ownerPhone.toString(),
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
