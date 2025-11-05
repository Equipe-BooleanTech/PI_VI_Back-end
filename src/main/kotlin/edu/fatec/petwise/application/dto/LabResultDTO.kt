package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.LabResult
import edu.fatec.petwise.domain.entity.LabTest
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Data Transfer Object para resultados de exames laboratoriais")
data class LabResultDTO(

    @Schema(description = "Identificador único do resultado do exame", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: UUID? = null,

    @NotNull
    @Schema(description = "Identificador do pet", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
    val petId: UUID,

    @NotNull
    @Schema(description = "Identificador do exame", example = "550e8400-e29b-41d4-a716-446655440002", required = true)
    val testId: UUID,

    @Schema(description = "Nome do exame realizado", example = "Hemograma Completo")
    val testName: String,

    @NotNull
    @Schema(description = "Data e hora do resultado", example = "2025-11-04T19:53:50", required = true)
    val resultDate: LocalDateTime,

    @NotBlank
    @Schema(description = "Valor do resultado do exame", example = "5.8", required = true)
    val resultValue: String,

    @NotNull
    @Schema(description = "Unidade do resultado", example = "g/dL", required = true)
    val resultUnit: String,

    @NotNull
    @Schema(description = "Valor de referência", example = "3.8 - 5.4 g/dL", required = true)
    val referenceValue: String,

    @NotNull
    @Schema(description = "Status do resultado", example = "HIGH", required = true)
    val status: Status,

    @NotNull
    @Schema(description = "Identificador do veterinário responsável", example = "550e8400-e29b-41d4-a716-446655440003", required = true)
    val veterinarianId: UUID,

    @Schema(description = "Nome do veterinário responsável", example = "Dr. João Silva")
    val veterinarianName: String,

    @Size(max = 1000)
    @Schema(description = "Observações adicionais", example = "Resultado fora da normalidade. Recomenda-se nova coleta em 15 dias.")
    val notes: String? = null
) {
    
    @Schema(description = "Enumeração dos possíveis status de resultados")
    enum class Status(val description: String) {
        NORMAL("Normal"),
        HIGH("Elevado"),
        LOW("Abaixo do normal"),
        ABNORMAL("Anormal")
    }
    
    companion object {
        
        fun toEntity(
            dto: LabResultDTO,
            labTest: LabTest,
            pet: Pet,
            veterinarian: User
        ): LabResult? {
            return dto.id?.let {
                LabResult(
                    id = it,
                    petId = dto.petId,
                    testId = dto.testId,
                    resultDate = dto.resultDate,
                    resultValue = dto.resultValue,
                    resultUnit = dto.resultUnit,
                    referenceValue = dto.referenceValue,
                    status = dto.status.name,
                    veterinarianId = dto.veterinarianId,
                    notes = dto.notes,
                    pet = pet,
                    labTest = labTest,
                    veterinarian = veterinarian
                )
            }
        }
        
        fun fromEntity(entity: LabResult): LabResultDTO {
            return LabResultDTO(
                id = entity.id,
                petId = entity.petId,
                testId = entity.testId,
                testName = entity.labTest.testName,
                resultDate = entity.resultDate,
                resultValue = entity.resultValue,
                resultUnit = entity.resultUnit,
                referenceValue = entity.referenceValue,
                status = Status.valueOf(entity.status),
                veterinarianId = entity.veterinarianId,
                veterinarianName = entity.veterinarian.fullName,
                notes = entity.notes
            )
        }
    }
}