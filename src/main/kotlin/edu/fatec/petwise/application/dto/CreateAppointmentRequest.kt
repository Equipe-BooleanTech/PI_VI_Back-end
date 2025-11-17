package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.ConsultaType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonFormat

data class CreateAppointmentRequest(
    @field:NotNull(message = "ID do pet é obrigatório")
    val petId: UUID,

    @field:NotBlank(message = "Nome do pet é obrigatório")
    @field:Size(max = 100, message = "Nome do pet deve ter no máximo 100 caracteres")
    val petName: String,

    @field:NotBlank(message = "Nome do veterinário é obrigatório")
    @field:Size(max = 100, message = "Nome do veterinário deve ter no máximo 100 caracteres")
    val veterinarianName: String,

    @field:NotNull(message = "Tipo da consulta é obrigatório")
    val consultaType: ConsultaType,

    @field:NotNull(message = "Data da consulta é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val consultaDate: LocalDateTime,

    @field:NotBlank(message = "Horário da consulta é obrigatório")
    val consultaTime: String,

    @field:Size(max = 1000, message = "Sintomas devem ter no máximo 1000 caracteres")
    val symptoms: String = "",

    @field:Size(max = 1000, message = "Notas devem ter no máximo 1000 caracteres")
    val notes: String = "",

    val price: Double = 0.0
)
