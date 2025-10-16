package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.ConsultaType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateAppointmentRequest(
    @field:NotBlank(message = "{appointment.petId.required}")
    val petId: String,
    
    @field:NotNull(message = "{appointment.consultaType.required}")
    val consultaType: ConsultaType,
    
    @field:NotBlank(message = "{appointment.consultaDate.required}")
    val consultaDate: String,
    
    @field:NotBlank(message = "{appointment.consultaTime.required}")
    val consultaTime: String,
    
    val symptoms: String = "",
    val notes: String = "",
    
    @field:Min(value = 0, message = "{appointment.price.min}")
    val price: Float = 0f
)

data class UpdateAppointmentRequest(
    val consultaDate: String? = null,
    val consultaTime: String? = null,
    val symptoms: String? = null,
    val notes: String? = null,
    val price: Float? = null
)

data class CompleteAppointmentRequest(
    @field:NotBlank(message = "{appointment.diagnosis.required}")
    val diagnosis: String,
    
    val treatment: String = "",
    val prescriptions: String = ""
)

data class RescheduleAppointmentRequest(
    @field:NotBlank(message = "{appointment.consultaDate.required}")
    val newDate: String,
    
    @field:NotBlank(message = "{appointment.consultaTime.required}")
    val newTime: String
)

data class AppointmentResponse(
    val id: String,
    val petId: String,
    val petName: String,
    val veterinarianName: String,
    val consultaType: String,
    val consultaDate: String,
    val consultaTime: String,
    val status: String,
    val symptoms: String,
    val diagnosis: String,
    val treatment: String,
    val prescriptions: String,
    val notes: String,
    val nextAppointment: String?,
    val price: Float,
    val isPaid: Boolean,
    val ownerName: String,
    val ownerPhone: String,
    val createdAt: String,
    val updatedAt: String
)
