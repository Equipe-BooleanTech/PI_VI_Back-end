package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentListResponse
import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ListAppointmentsUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(status: ConsultaStatus? = null, page: Int = 1, pageSize: Int = 20): AppointmentListResponse {
        val pageable: Pageable = PageRequest.of(page - 1, pageSize) 

        val appointmentsPage: Page<edu.fatec.petwise.domain.entity.Appointment> = if (status != null) {
            appointmentRepository.findByStatus(status, pageable)
        } else {
            appointmentRepository.findAll(pageable)
        }

        val appointmentResponses = appointmentsPage.content.map { appointment ->
            AppointmentResponse.fromEntity(Optional.of(appointment))
        }

        logger.info("Listadas ${appointmentResponses.size} consultas (página $page de ${appointmentsPage.totalPages})")

        return AppointmentListResponse(
            consultas = appointmentResponses,
            total = appointmentsPage.totalElements.toInt(),
            page = page,
            pageSize = pageSize
        )
    }
}
