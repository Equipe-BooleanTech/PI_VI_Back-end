package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.UpdateAppointmentRequest
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Service
class UpdateAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, appointmentId: UUID, request: UpdateAppointmentRequest): AppointmentResponse {
        val user = userRepository.findById(userId).orElseThrow { Exception("Usuário não encontrado") }

        val appointment = appointmentRepository.findById(appointmentId).orElseThrow { Exception("Consulta não encontrada") }

        // Check permissions
        if (user.userType == UserType.VETERINARY) {
            // VETERINARY can update any appointment
            if (!appointment.canUpdate()) {
                throw IllegalStateException(
                    "Consultas com status ${appointment.status} não podem ser atualizadas. " +
                    "Apenas consultas agendadas ou em andamento podem ser modificadas."
                )
            }
        } else if (user.userType == UserType.OWNER) {
            // OWNERS can only cancel their appointments
            if (request.status != ConsultaStatus.CANCELLED) {
                throw IllegalArgumentException("Proprietários só podem cancelar consultas")
            }
            // Check if the appointment belongs to one of their pets
            // This would require checking if the pet belongs to the owner
            // For now, we'll assume they can cancel any appointment (simplified)
        } else {
            throw IllegalArgumentException("Usuário não tem permissão para atualizar consultas")
        }

        // Apply updates
        request.petName?.let { appointment.petName = it.trim() }
        request.veterinarianName?.let { appointment.veterinarianName = it.trim() }
        request.consultaType?.let { appointment.consultaType = it }
        request.consultaDate?.let { appointment.consultaDate = it }
        request.consultaTime?.let { appointment.consultaTime = it }
        request.status?.let { appointment.status = it }
        request.symptoms?.let { appointment.symptoms = it.trim() }
        request.diagnosis?.let { appointment.diagnosis = it.trim() }
        request.treatment?.let { appointment.treatment = it.trim() }
        request.prescriptions?.let { appointment.prescriptions = it.trim() }
        request.notes?.let { appointment.notes = it.trim() }
        request.nextAppointment?.let { appointment.nextAppointment = it }
        request.price?.let { appointment.price = it }
        request.isPaid?.let { appointment.isPaid = it }

        appointment.updatedAt = LocalDateTime.now()

        val updatedAppointment = appointmentRepository.save(appointment)

        logger.info("Consulta $appointmentId atualizada pelo usuário $userId (${user.userType})")

        return AppointmentResponse.fromEntity(Optional.of(updatedAppointment))
    }
}
