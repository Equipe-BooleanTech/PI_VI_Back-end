package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Service
class UpdateAppointmentStatusUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    fun execute(id: UUID, newStatus: String, authentication: Authentication): AppointmentResponse {
        val userId = authentication.principal
        
        // Buscar consulta
        val appointment = appointmentRepository.findByIdAndOwnerId(id, userId as UUID)
            ?: throw IllegalArgumentException("Consulta não encontrada ou não pertence ao usuário")
        
        // Parse status
        val status = try {
            ConsultaStatus.valueOf(newStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Status inválido: $newStatus")
        }
        
        // Verificar transições válidas de status
        validateStatusTransition(appointment.status, status)
        
        // Atualizar status
        appointment.status = status
        appointment.updatedAt = LocalDateTime.now()
        
        val savedAppointment = appointmentRepository.save(appointment)
        return AppointmentResponse.fromEntity(Optional.of(savedAppointment))
    }
    
    private fun validateStatusTransition(currentStatus: ConsultaStatus, newStatus: ConsultaStatus) {
        // Regras de transição de status
        when (currentStatus) {
            ConsultaStatus.SCHEDULED -> {
                if (newStatus !in listOf(
                    ConsultaStatus.CONFIRMED,
                    ConsultaStatus.CANCELLED
                )) {
                    throw IllegalArgumentException("Transição inválida de ${currentStatus.name} para ${newStatus.name}")
                }
            }
            ConsultaStatus.CONFIRMED -> {
                if (newStatus !in listOf(
                    ConsultaStatus.IN_PROGRESS,
                    ConsultaStatus.CANCELLED
                )) {
                    throw IllegalArgumentException("Transição inválida de ${currentStatus.name} para ${newStatus.name}")
                }
            }
            ConsultaStatus.IN_PROGRESS -> {
                if (newStatus !in listOf(
                    ConsultaStatus.COMPLETED,
                    ConsultaStatus.NO_SHOW
                )) {
                    throw IllegalArgumentException("Transição inválida de ${currentStatus.name} para ${newStatus.name}")
                }
            }
            ConsultaStatus.COMPLETED,
            ConsultaStatus.CANCELLED,
            ConsultaStatus.NO_SHOW,
            ConsultaStatus.RESCHEDULED -> {
                throw IllegalArgumentException("Não é possível alterar status de ${currentStatus.name}")
            }
        }
    }
}
