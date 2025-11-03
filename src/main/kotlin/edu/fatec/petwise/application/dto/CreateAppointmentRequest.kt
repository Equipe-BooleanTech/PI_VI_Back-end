package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID


data class CreateAppointmentRequest(
    @field:NotNull(message = "ID do pet é obrigatório")
    val petId: UUID,
    
    @field:NotNull(message = "ID do veterinário é obrigatório")
    val veterinaryId: UUID,
    
    @field:NotNull(message = "Data e hora da consulta são obrigatórias")
    @field:Future(message = "A consulta deve ser agendada para uma data futura")
    val appointmentDatetime: LocalDateTime,
    
    @field:Positive(message = "Duração deve ser um valor positivo")
    val durationMinutes: Int = 30,
    
    @field:NotBlank(message = "Motivo da consulta é obrigatório")
    @field:Size(max = 200, message = "Motivo deve ter no máximo 200 caracteres")
    val motivo: String,
    
    @field:Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    val observacoesCliente: String? = null
)
