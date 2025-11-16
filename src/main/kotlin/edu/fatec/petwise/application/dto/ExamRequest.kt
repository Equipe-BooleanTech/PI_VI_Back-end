package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ExamRequest(
    @field:NotBlank(message = "Tipo de exame é obrigatório")
    val examType: String,

    @field:NotNull(message = "Data do exame é obrigatória")
    val examDate: LocalDateTime,

    val results: String? = null,

    @field:NotBlank(message = "Status é obrigatório")
    val status: String,

    val notes: String? = null,
    val attachmentUrl: String? = null
)
