package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.CreateAppointmentRequest
import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.enums.UserType
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
    fun execute(userId: String, request: CreateAppointmentRequest): AppointmentResponse {
        val ownerId = UUID.fromString(userId)
        val pet = petRepository.findByIdAndOwnerId(request.petId, ownerId)
            ?: throw Exception("Pet não encontrado ou não pertence a você")
        
        if (!pet.ativo) {
            throw IllegalArgumentException("Não é possível agendar consulta para um pet inativo")
        }
        val veterinary = userRepository.findById(request.veterinaryId)
            ?: throw Exception("Veterinário não encontrado")
        
        if (veterinary.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("O usuário selecionado não é um veterinário")
        }
        
        if (!veterinary.active) {
            throw IllegalArgumentException("O veterinário selecionado está inativo")
        }
        val endTime = request.appointmentDatetime.plusMinutes(request.durationMinutes.toLong())
        val hasConflict = appointmentRepository.existsConflictingAppointment(
            veterinaryId = request.veterinaryId,
            startTime = request.appointmentDatetime,
            endTime = endTime
        )
        
        if (hasConflict) {
            throw IllegalArgumentException(
                "O veterinário já possui uma consulta agendada neste horário. " +
                "Por favor, escolha outro horário."
            )
        }
        val appointment = Appointment(
            petId = request.petId,
            ownerId = ownerId,
            veterinaryId = request.veterinaryId,
            appointmentDatetime = request.appointmentDatetime,
            durationMinutes = request.durationMinutes,
            motivo = request.motivo.trim(),
            observacoesCliente = request.observacoesCliente?.trim()
        )
        
        val savedAppointment = appointmentRepository.save(appointment)
        
        logger.info("Consulta criada: ID=${savedAppointment.id}, Pet=${pet.nome}, Veterinário=${veterinary.fullName}")
        
        return AppointmentResponse.fromEntity(
            appointment = savedAppointment,
            petNome = pet.nome,
            veterinaryNome = veterinary.fullName
        )
    }
}
