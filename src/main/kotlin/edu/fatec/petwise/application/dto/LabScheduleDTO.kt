package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.LabSchedule
import edu.fatec.petwise.domain.entity.LabTest
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID


@Schema(description = "DTO para agendamento de exames laboratoriais")
data class LabScheduleDTO(
    @Schema(description = "ID único do agendamento", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID? = null,

    @Schema(description = "ID do pet", example = "123e4567-e89b-12d3-a456-426614174001")
    @field:NotNull
    val petId: UUID,

    @Schema(description = "Nome do pet", example = "Rex")
    val petName: String,

    @Schema(description = "ID do exame laboratorial", example = "123e4567-e89b-12d3-a456-426614174002")
    @field:NotNull
    val testId: UUID,

    @Schema(description = "Nome do exame laboratorial", example = "Hemograma Completo")
    val testName: String,

    @Schema(description = "ID do veterinário", example = "123e4567-e89b-12d3-a456-426614174003")
    @field:NotNull
    val veterinarianId: UUID,

    @Schema(description = "Nome do veterinário", example = "Dr. João Silva")
    val veterinarianName: String,

    @Schema(description = "Data do agendamento", example = "2024-12-15T10:30:00")
    @field:NotNull
    val scheduleDate: LocalDateTime,

    @Schema(description = "Horário do agendamento", example = "10:30:00")
    @field:NotNull
    val appointmentTime: LocalTime,

    @Schema(description = "Duração do exame em minutos", example = "30")
    @field:NotNull
    @field:Min(1)
    val duration: Integer,

    @Schema(description = "Status do agendamento", example = "SCHEDULED")
    @field:NotNull
    val status: String,

    @Schema(description = "Prioridade do agendamento", example = "ROUTINE")
    @field:NotNull
    val priority: String,

    @Schema(description = "Observações do agendamento", example = "Pet deve estar em jejum de 12 horas")
    @field:Size(max = 1000)
    val notes: String? = null,

    @Schema(description = "Data de confirmação do agendamento", example = "2024-12-14T15:20:00")
    val confirmationDate: LocalDateTime? = null,

    @Schema(description = "Data de conclusão do exame", example = "2024-12-15T11:00:00")
    val completedAt: LocalDateTime? = null
) {
    companion object {
        /**
         * Converte um DTO para entidade LabSchedule
         */
        fun toEntity(
            dto: LabScheduleDTO,
            pet: Pet,
            labTest: LabTest,
            veterinarian: User
        ): LabSchedule? {
            return dto.id?.let {
                LabSchedule(
                    id = it,
                    pet = pet,
                    labTest = labTest,
                    veterinarian = veterinarian,
                    scheduleDate = dto.scheduleDate,
                    appointmentTime = dto.appointmentTime,
                    duration = dto.duration,
                    status = dto.status,
                    priority = dto.priority,
                    notes = dto.notes,
                    confirmationDate = dto.confirmationDate,
                    completedAt = dto.completedAt
                )
            }
        }


        fun fromEntity(entity: LabSchedule): LabScheduleDTO? {
            return entity.labTest.id?.let {
                entity.veterinarian.id?.let { it1 ->
                    LabScheduleDTO(
                        id = entity.id,
                        petId = entity.pet.id,
                        petName = entity.pet.nome,
                        testId = it,
                        testName = entity.labTest.testName,
                        veterinarianId = it1,
                        veterinarianName = entity.veterinarian.fullName,
                        scheduleDate = entity.scheduleDate,
                        appointmentTime = entity.appointmentTime,
                        duration = entity.duration,
                        status = entity.status,
                        priority = entity.priority,
                        notes = entity.notes,
                        confirmationDate = entity.confirmationDate,
                        completedAt = entity.completedAt
                    )
                }
            }
        }
    }
}