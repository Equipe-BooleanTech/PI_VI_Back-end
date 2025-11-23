package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.enums.ConsultaType
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UpdateAppointmentRequest(
    @field:Size(max = 100, message = "Nome do pet deve ter no máximo 100 caracteres")
    val petName: String? = null,

    @field:Size(max = 100, message = "Nome do veterinário deve ter no máximo 100 caracteres")
    val veterinarianName: String? = null,

    val consultaType: ConsultaType? = null,

    val consultaDate: LocalDateTime? = null,

    val consultaTime: String? = null,

    val status: ConsultaStatus? = null,

    @field:Size(max = 1000, message = "Sintomas devem ter no máximo 1000 caracteres")
    val symptoms: String? = null,

    @field:Size(max = 1000, message = "Diagnóstico deve ter no máximo 1000 caracteres")
    val diagnosis: String? = null,

    @field:Size(max = 1000, message = "Tratamento deve ter no máximo 1000 caracteres")
    val treatment: String? = null,

    @field:Size(max = 1000, message = "Prescrições devem ter no máximo 1000 caracteres")
    val prescriptions: String? = null,

    @field:Size(max = 1000, message = "Notas devem ter no máximo 1000 caracteres")
    val notes: String? = null,

    val nextAppointment: LocalDateTime? = null,

    val price: Double? = null,

    val isPaid: Boolean? = null
)
