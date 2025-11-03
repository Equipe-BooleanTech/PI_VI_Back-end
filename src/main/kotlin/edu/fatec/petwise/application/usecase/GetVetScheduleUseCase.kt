package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class GetVetScheduleUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(
        authentication: Authentication,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): List<AppointmentResponse> {

        val veterinaryId = UUID.fromString(authentication.principal.toString())
        val start = startDate?.atStartOfDay() ?: LocalDate.now().atStartOfDay()
        val end = (endDate ?: LocalDate.now().plusDays(7)).atTime(23, 59, 59)

        val appointments = appointmentRepository
            .findByVeterinaryIdAndAppointmentDatetimeBetweenOrderByAppointmentDatetimeAsc(
                veterinaryId,
                start,
                end
            )

        return appointments.map { appointment ->
            val pet = petRepository.findById(appointment.petId).orElse(null)
            val vet = userRepository.findById(appointment.veterinaryId)

            AppointmentResponse(
                id = appointment.id,
                petId = appointment.petId,
                ownerId = appointment.ownerId,
                veterinaryId = appointment.veterinaryId,
                appointmentDatetime = appointment.appointmentDatetime,
                durationMinutes = appointment.durationMinutes,
                motivo = appointment.motivo,
                status = appointment.status,
                observacoesCliente = appointment.observacoesCliente,
                observacoesVeterinario = appointment.observacoesVeterinario,
                valor = appointment.valor,
                createdAt = appointment.createdAt,
                updatedAt = appointment.updatedAt,
                petNome = pet?.nome,
                veterinaryNome = vet?.fullName
            )
        }
    }
}
