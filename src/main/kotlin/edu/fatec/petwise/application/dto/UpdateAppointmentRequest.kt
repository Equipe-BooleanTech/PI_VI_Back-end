package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID
data class UpdateAppointmentRequest(
    val veterinaryId: UUID? = null,
    
    @field:Future(message = "A consulta deve ser agendada para uma data futura")
    val appointmentDatetime: LocalDateTime? = null,
    
    @field:Positive(message = "Duração deve ser um valor positivo")
    val durationMinutes: Int? = null,
    
    @field:Size(max = 200, message = "Motivo deve ter no máximo 200 caracteres")
    val motivo: String? = null,
    
    val status: AppointmentStatus? = null,
    
    @field:Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    val observacoesCliente: String? = null,
    
    @field:Size(max = 2000, message = "Observações do veterinário devem ter no máximo 2000 caracteres")
    val observacoesVeterinario: String? = null,
    
    @field:Positive(message = "Valor deve ser positivo")
    val valor: Double? = null
)
