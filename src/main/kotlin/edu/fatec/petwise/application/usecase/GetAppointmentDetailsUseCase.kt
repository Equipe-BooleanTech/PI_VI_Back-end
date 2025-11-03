package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetAppointmentDetailsUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, appointmentId: String): AppointmentResponse {
        val ownerId = UUID.fromString(userId)
        val appointmentUuid = UUID.fromString(appointmentId)
        
        val appointment = appointmentRepository.findByIdAndOwnerId(appointmentUuid, ownerId)
            ?: throw Exception("Consulta não encontrada ou você não tem permissão para acessá-la")
        
        // Obter nomes do pet e veterinário
        val petNome = petRepository.findById(appointment.petId).orElse(null)?.nome
        val veterinaryNome = userRepository.findById(appointment.veterinaryId)?.fullName
        
        logger.info("Detalhes da consulta $appointmentId obtidos pelo usuário $userId")
        
        return AppointmentResponse.fromEntity(appointment, petNome, veterinaryNome)
    }
}
