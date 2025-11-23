package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.repository.AppointmentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetAppointmentDetailsUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(appointmentId: UUID): AppointmentResponse {
        val appointment = appointmentRepository.findById(appointmentId)
            ?: throw Exception("Consulta não encontrada")

        logger.info("Detalhes da consulta $appointmentId obtidos")

        return AppointmentResponse.fromEntity(appointment)
    }
}
