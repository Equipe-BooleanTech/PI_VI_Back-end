package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListUserAppointmentsUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, status: AppointmentStatus? = null): List<AppointmentResponse> {
        val ownerId = UUID.fromString(userId)
        
        val appointments = if (status != null) {
            appointmentRepository.findByOwnerIdAndStatus(ownerId, status)
        } else {
            appointmentRepository.findByOwnerIdOrderByAppointmentDatetimeDesc(ownerId)
        }
        
        logger.info("Listadas ${appointments.size} consultas para o usuário $userId")
        
        // Enriquecer com nomes de pets e veterinários
        return appointments.map { appointment ->
            val petNome = petRepository.findById(appointment.petId).orElse(null)?.nome
            val veterinaryNome = userRepository.findById(appointment.veterinaryId)?.fullName
            
            AppointmentResponse.fromEntity(appointment, petNome, veterinaryNome)
        }
    }
}
