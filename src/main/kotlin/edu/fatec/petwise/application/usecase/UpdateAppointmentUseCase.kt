package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.UpdateAppointmentRequest
import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.domain.entity.UserType
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.infrastructure.exception.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdateAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun execute(userId: String, appointmentId: String, request: UpdateAppointmentRequest): AppointmentResponse {
        val ownerId = UUID.fromString(userId)
        val appointmentUuid = UUID.fromString(appointmentId)
        
        val appointment = appointmentRepository.findByIdAndOwnerId(appointmentUuid, ownerId)
            ?: throw Exception("Consulta não encontrada ou você não tem permissão para atualizá-la")
        
        // Verificar se a consulta pode ser atualizada
        if (!appointment.podeAtualizar()) {
            throw IllegalStateException(
                "Consultas com status ${appointment.status} não podem ser atualizadas. " +
                "Apenas consultas AGENDADA ou CONFIRMADA podem ser modificadas."
            )
        }
        
        // Validar novo veterinário se fornecido
        request.veterinaryId?.let { newVeterinaryId ->
            val veterinary = userRepository.findById(newVeterinaryId)
                ?: throw Exception("Veterinário não encontrado")
            
            if (veterinary.userType != UserType.VETERINARY) {
                throw IllegalArgumentException("O usuário selecionado não é um veterinário")
            }
            
            if (!veterinary.active) {
                throw IllegalArgumentException("O veterinário selecionado está inativo")
            }
        }
        
        // Verificar conflito de horário se data/hora ou veterinário forem alterados
        if (request.appointmentDatetime != null || request.veterinaryId != null || request.durationMinutes != null) {
            val newDatetime = request.appointmentDatetime ?: appointment.appointmentDatetime
            val newVeterinaryId = request.veterinaryId ?: appointment.veterinaryId
            val newDuration = request.durationMinutes ?: appointment.durationMinutes
            val endTime = newDatetime.plusMinutes(newDuration.toLong())
            
            // Ignorar conflito com a própria consulta
            val hasConflict = appointmentRepository.existsConflictingAppointment(
                veterinaryId = newVeterinaryId,
                startTime = newDatetime,
                endTime = endTime
            )
            
            if (hasConflict) {
                // Verificar se o conflito é com outra consulta ou com a própria
                val conflictingAppointments = appointmentRepository
                    .findByVeterinaryIdOrderByAppointmentDatetimeDesc(newVeterinaryId)
                    .filter { it.id != appointmentUuid }
                    .filter { it.status !in listOf(AppointmentStatus.CANCELADA, AppointmentStatus.NAO_COMPARECEU) }
                    .filter { 
                        it.appointmentDatetime >= newDatetime && it.appointmentDatetime < endTime ||
                        newDatetime >= it.appointmentDatetime && newDatetime < it.appointmentDatetime.plusMinutes(it.durationMinutes.toLong())
                    }
                
                if (conflictingAppointments.isNotEmpty()) {
                    throw IllegalArgumentException(
                        "O veterinário já possui uma consulta agendada neste horário. " +
                        "Por favor, escolha outro horário."
                    )
                }
            }
        }
        
        // Atualizar apenas campos fornecidos
        request.veterinaryId?.let { appointment.veterinaryId = it }
        request.appointmentDatetime?.let { appointment.appointmentDatetime = it }
        request.durationMinutes?.let { appointment.durationMinutes = it }
        request.motivo?.let { appointment.motivo = it.trim() }
        request.status?.let { appointment.status = AppointmentStatus.valueOf(toString()) }
        request.observacoesCliente?.let { appointment.observacoesCliente = it.trim() }
        request.observacoesVeterinario?.let { appointment.observacoesVeterinario = it.trim() }
        request.valor?.let { appointment.valor = it }
        
        val updatedAppointment = appointmentRepository.save(appointment)
        
        // Obter nomes do pet e veterinário
        val petNome = petRepository.findById(appointment.petId).orElse(null)?.nome
        val veterinaryNome = userRepository.findById(appointment.veterinaryId)?.fullName
        
        logger.info("Consulta $appointmentId atualizada pelo usuário $userId")
        
        return AppointmentResponse.fromEntity(updatedAppointment, petNome, veterinaryNome)
    }
}
