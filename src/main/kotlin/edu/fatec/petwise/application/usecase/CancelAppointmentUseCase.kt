package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID
@Service
class CancelAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, appointmentId: String): MessageResponse {
        val ownerId = UUID.fromString(userId)
        val appointmentUuid = UUID.fromString(appointmentId)
        
        val appointment = appointmentRepository.findByIdAndOwnerId(appointmentUuid, ownerId)
            ?: throw Exception("Consulta não encontrada ou você não tem permissão para cancelá-la")
        
        // Verificar se a consulta pode ser cancelada
        if (!appointment.podeCancelar()) {
            throw IllegalStateException(
                "Consultas com status ${appointment.status} não podem ser canceladas. " +
                "Apenas consultas AGENDADA ou CONFIRMADA podem ser canceladas."
            )
        }
        
        // Alterar status para CANCELADA
        appointment.status = AppointmentStatus.CANCELADA
        appointmentRepository.save(appointment)
        
        logger.info("Consulta $appointmentId cancelada pelo usuário $userId")
        
        return MessageResponse("Consulta cancelada com sucesso")
    }
}
