package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.repository.AppointmentRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.UUID

@Service
class UpdateAppointmentStatusUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    fun execute(id: UUID, newStatus: String, authentication: Authentication): AppointmentResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        
        // Buscar consulta
        val appointment = appointmentRepository.findByIdAndOwnerId(id, userId)
            ?: throw IllegalArgumentException("Consulta não encontrada ou não pertence ao usuário")
        

        
        // Verificar transições válidas de status
        validateStatusTransition(appointment.status, status)
        
        // Atualizar status
        val updatedAppointment = appointment.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        
        val savedAppointment = appointmentRepository.save(updatedAppointment)
        return savedAppointment.toAppointmentResponse()
    }
    
    private fun validateStatusTransition(currentStatus: Appointment.AppointmentStatus, newStatus: Appointment.AppointmentStatus) {
        // Regras de transição de status
        when (currentStatus) {
            Appointment.AppointmentStatus.AGENDADA -> {
                if (newStatus !in listOf(
                    Appointment.AppointmentStatus.CONFIRMADA,
                    Appointment.AppointmentStatus.CANCELADA
                )) {
                    throw IllegalArgumentException("Transição inválida de ${currentStatus.name} para ${newStatus.name}")
                }
            }
            Appointment.AppointmentStatus.CONFIRMADA -> {
                if (newStatus !in listOf(
                    Appointment.AppointmentStatus.EM_ANDAMENTO,
                    Appointment.AppointmentStatus.CANCELADA
                )) {
                    throw IllegalArgumentException("Transição inválida de ${currentStatus.name} para ${newStatus.name}")
                }
            }
            Appointment.AppointmentStatus.EM_ANDAMENTO -> {
                if (newStatus !in listOf(
                    Appointment.AppointmentStatus.CONCLUIDA,
                    Appointment.AppointmentStatus.FALTOU
                )) {
                    throw IllegalArgumentException("Transição inválida de ${currentStatus.name} para ${newStatus.name}")
                }
            }
            Appointment.AppointmentStatus.CONCLUIDA,
            Appointment.AppointmentStatus.CANCELADA,
            Appointment.AppointmentStatus.FALTOU -> {
                throw IllegalArgumentException("Não é possível alterar status de ${currentStatus.name}")
            }
        }
    }
}