package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ListAppointmentsUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(status: ConsultaStatus? = null): List<AppointmentResponse> {
        val appointments = if (status != null) {
            appointmentRepository.findByStatus(status)
        } else {
            appointmentRepository.findAll()
        }

        logger.info("Listadas ${appointments.size} consultas")

        return appointments.map { appointment ->
            AppointmentResponse.fromEntity(Optional.of(appointment))
        }
    }
}
