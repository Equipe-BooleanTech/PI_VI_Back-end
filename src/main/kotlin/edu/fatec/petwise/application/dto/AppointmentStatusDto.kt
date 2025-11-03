package edu.fatec.petwise.application.dto

data class UpdateAppointmentStatusRequest(
    val status: String,
    val notes: String? = null
)

data class CancelAppointmentRequest(
    val reason: String? = null
)

data class CancelAppointmentResponse(
    val consultaId: String,
    val status: String,
    val message: String
)
