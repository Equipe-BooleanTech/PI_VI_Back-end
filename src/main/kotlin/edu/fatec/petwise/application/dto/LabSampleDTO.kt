package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.LabSample
import edu.fatec.petwise.domain.entity.LabTest
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

import java.time.LocalDateTime
import java.util.UUID


@Schema(description = "DTO para representação de amostras de laboratório")
data class LabSampleDTO(
    @Schema(description = "ID único da amostra", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID? = null,

    @Schema(description = "Código da amostra", example = "LAB-2024-001")
    @field:NotNull(message = "sampleCode não pode ser nulo")
    val sampleCode: String,

    @Schema(description = "ID do pet", example = "123e4567-e89b-12d3-a456-426614174001")
    @field:NotNull(message = "petId não pode ser nulo")
    val petId: UUID,

    @Schema(description = "Nome do pet", example = "Rex")
    val petName: String,

    @Schema(description = "ID do teste laboratorial", example = "123e4567-e89b-12d3-a456-426614174002")
    @field:NotNull(message = "testId não pode ser nulo")
    val testId: UUID,

    @Schema(description = "Nome do teste", example = "Hemograma Completo")
    val testName: String,

    @Schema(description = "Tipo da amostra", example = "BLOOD")
    @field:NotNull(message = "sampleType não pode ser nulo")
    val sampleType: String, // BLOOD, URINE, FECAL, TISSUE, SWAB

    @Schema(description = "Data e hora da coleta", example = "2024-01-15T10:30:00")
    @field:NotNull(message = "collectionDate não pode ser nulo")
    val collectionDate: LocalDateTime,

    @Schema(description = "Local da coleta", example = "Clínica Veterinária Central")
    @field:Size(max = 255, message = "collectionLocation deve ter no máximo 255 caracteres")
    val collectionLocation: String? = null,

    @Schema(description = "Status da amostra", example = "COLLECTED")
    @field:NotNull(message = "status não pode ser nulo")
    val status: String, // COLLECTED, PROCESSING, ANALYZING, COMPLETED, REJECTED

    @Schema(description = "ID do técnico responsável", example = "123e4567-e89b-12d3-a456-426614174003")
    val technicianId: UUID? = null,

    @Schema(description = "Nome do técnico responsável", example = "João Silva")
    val technicianName: String? = null,

    @Schema(description = "Motivo da rejeição", example = "Amostra hemolisada")
    @field:Size(max = 500, message = "rejectionReason deve ter no máximo 500 caracteres")
    val rejectionReason: String? = null,

    @Schema(description = "Condições de armazenamento", example = "Refrigerado a 4°C")
    @field:Size(max = 255, message = "storageConditions deve ter no máximo 255 caracteres")
    val storageConditions: String? = null,

    @Schema(description = "Data de validade da amostra", example = "2024-01-20T10:30:00")
    val expirationDate: LocalDateTime? = null
) {
    companion object {

        fun toEntity(
            dto: LabSampleDTO,
            pet: Pet,
            labTest: LabTest,
            technician: User? = null
        ): LabSample {
            return LabSample(
                id = dto.id ?: UUID.randomUUID(),
                sampleCode = dto.sampleCode,
                pet = pet,
                test = labTest,
                sampleType = dto.sampleType,
                collectionDate = dto.collectionDate,
                collectionLocation = dto.collectionLocation,
                status = dto.status,
                technician = technician,
                rejectionReason = dto.rejectionReason,
                storageConditions = dto.storageConditions,
                expirationDate = dto.expirationDate,
            )
        }

        fun fromEntity(entity: LabSample): LabSampleDTO? {
            return entity.test.id?.let {
                LabSampleDTO(
                    id = entity.id,
                    sampleCode = entity.sampleCode,
                    petId = entity.pet.id,
                    petName = entity.pet.nome,
                    sampleType = entity.sampleType,
                    collectionDate = entity.collectionDate,
                    collectionLocation = entity.collectionLocation,
                    status = entity.status,
                    technicianId = entity.technician?.id,
                    technicianName = entity.technician?.fullName,
                    rejectionReason = entity.rejectionReason,
                    storageConditions = entity.storageConditions,
                    expirationDate = entity.expirationDate,
                    testId = it,
                    testName = entity.test.testName,
                )
            }
        }
    }

}

