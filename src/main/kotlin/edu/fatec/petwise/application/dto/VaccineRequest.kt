package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.VaccinationStatus
import edu.fatec.petwise.domain.enums.VaccineType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonFormat

data class VaccineRequest(
    @field:NotNull(message = "Tipo de vacina é obrigatório")
    val vaccineType: VaccineType,

    @field:NotNull(message = "Data da vacinação é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val vaccinationDate: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val nextDoseDate: LocalDateTime? = null,

    @field:Positive(message = "Total de doses deve ser positivo")
    val totalDoses: Int,

    val manufacturer: String? = null,
    val observations: String = "",

    @field:NotNull(message = "Status é obrigatório")
    val status: VaccinationStatus
)
