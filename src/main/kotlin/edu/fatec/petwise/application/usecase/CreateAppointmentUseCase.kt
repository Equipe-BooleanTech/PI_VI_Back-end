package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.CreateAppointmentRequest
import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Service
class CreateAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, request: CreateAppointmentRequest): AppointmentResponse {
        val user = userRepository.findById(userId).orElseThrow { Exception("Usuário não encontrado") }

        
        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem criar consultas")
        }

        val pet = petRepository.findById(request.petId).orElseThrow { Exception("Pet não encontrado") }

        val appointment = Appointment(
            id = null,
            petId = request.petId,
            ownerId = pet.ownerId,
            petName = request.petName.trim(),
            veterinarianName = request.veterinarianName.trim(),
            consultaType = request.consultaType,
            consultaDate = request.consultaDate,
            consultaTime = request.consultaTime,
            status = ConsultaStatus.SCHEDULED,
            symptoms = request.symptoms.trim(),
            notes = request.notes.trim(),
            price = request.price,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedAppointment = appointmentRepository.save(appointment)

        logger.info("Consulta criada: ID=${savedAppointment.id}, Pet=${savedAppointment.petName}, Veterinário=${savedAppointment.veterinarianName}")

        return AppointmentResponse.fromEntity(Optional.of(savedAppointment))
    }
}
