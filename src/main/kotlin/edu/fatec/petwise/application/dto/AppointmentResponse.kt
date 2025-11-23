package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.enums.ConsultaType
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

data class AppointmentResponse(
    val id: UUID?,
    val petId: UUID,
    val petName: String,
    val veterinarianName: String,
    val consultaType: ConsultaType,
    val consultaDate: LocalDateTime,
    val consultaTime: String,
    val status: ConsultaStatus,
    val symptoms: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val prescriptions: String = "",
    val notes: String = "",
    val nextAppointment: LocalDateTime? = null,
    val price: Double = 0.0,
    val isPaid: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(appointment: Optional<Appointment>): AppointmentResponse {
            val app = appointment.get()
            return AppointmentResponse(
                id = app.id,
                petId = app.petId,
                petName = app.petName,
                veterinarianName = app.veterinarianName,
                consultaType = app.consultaType,
                consultaDate = app.consultaDate,
                consultaTime = app.consultaTime,
                status = app.status,
                symptoms = app.symptoms,
                diagnosis = app.diagnosis,
                treatment = app.treatment,
                prescriptions = app.prescriptions,
                notes = app.notes,
                nextAppointment = app.nextAppointment,
                price = app.price,
                isPaid = app.isPaid,
                createdAt = app.createdAt,
                updatedAt = app.updatedAt
            )
        }
    }
}
