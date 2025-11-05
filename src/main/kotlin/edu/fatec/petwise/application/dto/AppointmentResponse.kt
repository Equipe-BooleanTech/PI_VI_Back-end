package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.entity.AppointmentStatus
import java.time.LocalDateTime
import java.util.UUID

data class AppointmentResponse(
    val id: UUID,
    val petId: UUID,
    val petNome: String?,
    val ownerId: UUID,
    val veterinaryId: UUID,
    val veterinaryNome: String?,
    val appointmentDatetime: LocalDateTime,
    val durationMinutes: Int,
    val motivo: String,
    val status: AppointmentStatus,
    val observacoesCliente: String?,
    val observacoesVeterinario: String?,
    val valor: Double?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {

        fun fromEntity(
            appointment: Appointment,
            petNome: String? = null,
            veterinaryNome: String? = null
        ): AppointmentResponse {
            return AppointmentResponse(
                id = UUID.fromString(appointment.id.toString()),
                petId =  UUID.fromString(appointment.petId.toString()),
                petNome = petNome,
                ownerId = appointment.ownerId,
                veterinaryId = appointment.veterinaryId,
                veterinaryNome = veterinaryNome,
                appointmentDatetime = appointment.appointmentDatetime,
                durationMinutes = appointment.durationMinutes,
                motivo = appointment.motivo,
                status = appointment.status,
                observacoesCliente = appointment.observacoesCliente,
                observacoesVeterinario = appointment.observacoesVeterinario,
                valor = appointment.valor,
                createdAt = appointment.createdAt,
                updatedAt = appointment.updatedAt
            )
        }
    }
}
