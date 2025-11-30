package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.entity.ConsultaFilterOptions
import edu.fatec.petwise.domain.repository.AppointmentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class FilterAppointmentsUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(options: ConsultaFilterOptions): List<AppointmentResponse> {
        var appointments = appointmentRepository.findAll()

        
        if (options.consultaType != null) {
            appointments = appointments.filter { it.consultaType == options.consultaType }
        }

        if (options.status != null) {
            appointments = appointments.filter { it.status == options.status }
        }

        if (options.petId != null) {
            appointments = appointments.filter { it.petId == options.petId }
        }

        if (options.dateRange != null) {
            appointments = appointments.filter { appointment ->
                appointment.consultaDate >= options.dateRange.startDate &&
                appointment.consultaDate <= options.dateRange.endDate
            }
        }

        if (options.searchQuery.isNotBlank()) {
            appointments = appointments.filter { appointment ->
                appointment.petName.contains(options.searchQuery, ignoreCase = true) ||
                appointment.veterinarianName.contains(options.searchQuery, ignoreCase = true) ||
                appointment.symptoms.contains(options.searchQuery, ignoreCase = true) ||
                appointment.diagnosis.contains(options.searchQuery, ignoreCase = true)
            }
        }

        logger.info("Filtrados ${appointments.size} appointments com opções: $options")

        return appointments.map { AppointmentResponse.fromEntity(Optional.of(it)) }
    }
}
