package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CancelAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, appointmentId: UUID): MessageResponse {
        val user = userRepository.findById(userId).orElseThrow { Exception("Usuário não encontrado") }

        val appointment = appointmentRepository.findById(appointmentId).orElse(null)
            ?: throw Exception("Consulta não encontrada")

        
        if (user.userType != UserType.VETERINARY && user.userType != UserType.OWNER) {
            throw IllegalArgumentException("Usuário não tem permissão para cancelar consultas")
        }

        
        if (!appointment.canCancel()) {
            throw IllegalStateException(
                "Consultas com status ${appointment.status} não podem ser canceladas. " +
                "Apenas consultas agendadas ou em andamento podem ser canceladas."
            )
        }

        
        appointment.status = ConsultaStatus.CANCELLED
        appointmentRepository.save(appointment)

        logger.info("Consulta $appointmentId cancelada pelo usuário $userId (${user.userType})")

        return MessageResponse("Consulta cancelada com sucesso")
    }
}

