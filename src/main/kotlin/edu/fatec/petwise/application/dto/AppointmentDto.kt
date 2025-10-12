package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.AppointmentStatus
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateAppointmentRequest(
    @field:NotBlank(message = "{appointment.petId.required}")
    val petId: String,
    
    @field:NotBlank(message = "{appointment.veterinaryId.required}")
    val veterinaryId: String,
    
    @field:NotNull(message = "{appointment.scheduledDate.required}")
    @field:Future(message = "{appointment.scheduledDate.future}")
    val scheduledDate: LocalDateTime,
    
    @field:NotBlank(message = "{appointment.reason.required}")
    @field:Size(min = 5, max = 500, message = "{appointment.reason.size}")
    val reason: String,
    
    val notes: String? = null
)

data class UpdateAppointmentRequest(
    @field:Future(message = "{appointment.scheduledDate.future}")
    val scheduledDate: LocalDateTime? = null,
    
    @field:Size(min = 5, max = 500, message = "{appointment.reason.size}")
    val reason: String? = null,
    
    val notes: String? = null
)

data class CompleteAppointmentRequest(
    @field:NotBlank(message = "{appointment.diagnosis.required}")
    val diagnosis: String,
    
    val treatment: String? = null
)

data class AppointmentResponse(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val ownerId: String,
    val scheduledDate: String,
    val reason: String,
    val notes: String?,
    val diagnosis: String?,
    val treatment: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
