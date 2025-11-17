package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonFormat

data class ExamRequest(
    @field:NotBlank(message = "Tipo de exame é obrigatório")
    val examType: String,

    @field:NotNull(message = "Data do exame é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val examDate: LocalDateTime,

    val results: String? = null,

    @field:NotBlank(message = "Status é obrigatório")
    val status: String,

    val notes: String? = null,
    val attachmentUrl: String? = null
)
