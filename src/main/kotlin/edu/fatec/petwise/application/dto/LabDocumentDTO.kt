package edu.fatec.petwise.application.dto


import edu.fatec.petwise.domain.entity.*
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

@Schema(
    title = "LabDocumentDTO",
    description = "Data Transfer Object para representação de documentos de laboratório"
)
data class LabDocumentDTO(
    @Schema(
        title = "ID do documento",
        description = "Identificador único do documento de laboratório",
        example = "550e8400-e29b-41d4-a716-446655440000",
        type = "string",
        format = "uuid"
    )
    val id: UUID? = null,
    @Schema(
        title = "Nome do arquivo",
        description = "Nome do arquivo no sistema",
        example = "lab_result_001.pdf",
        type = "string",
        maxLength = 255
    )
    @field:NotNull(message = "O nome do arquivo é obrigatório")
    @field:Size(max = 255, message = "O nome do arquivo deve ter no máximo 255 caracteres")
    val fileName: String,

    @Schema(
        title = "Nome original do arquivo",
        description = "Nome original do arquivo conforme enviado pelo usuário",
        example = "resultado_exame_joao.pdf",
        type = "string",
        maxLength = 255
    )
    @field:NotNull(message = "O nome original do arquivo é obrigatório")
    @field:Size(max = 255, message = "O nome original do arquivo deve ter no máximo 255 caracteres")
    val originalFileName: String,

    @Schema(
        title = "Tipo do arquivo",
        description = "Tipo MIME do arquivo",
        example = "application/pdf",
        type = "string",
        maxLength = 100
    )
    @field:NotNull(message = "O tipo do arquivo é obrigatório")
    @field:Size(max = 100, message = "O tipo do arquivo deve ter no máximo 100 caracteres")
    val fileType: String,

    @Schema(
        title = "Tamanho do arquivo",
        description = "Tamanho do arquivo em bytes",
        example = "1024",
        type = "integer",
        minimum = "0"
    )
    @field:NotNull(message = "O tamanho do arquivo é obrigatório")
    @field:Min(value = 0, message = "O tamanho do arquivo deve ser maior ou igual a zero")
    val fileSize: Long,

    @Schema(
        title = "Caminho do arquivo",
        description = "Caminho onde o arquivo está armazenado",
        example = "/uploads/lab/2025/01/15/lab_result_001.pdf",
        type = "string",
        maxLength = 500
    )
    @field:NotNull(message = "O caminho do arquivo é obrigatório")
    @field:Size(max = 500, message = "O caminho do arquivo deve ter no máximo 500 caracteres")
    val filePath: String,

    @Schema(
        title = "Tipo do documento",
        description = "Tipo do documento de laboratório",
        example = "LAB_RESULT",
        type = "string",
        allowableValues = ["LAB_RESULT", "LAB_REPORT", "LAB_CERTIFICATE", "LAB_IMAGE", "OTHER"]
    )
    @field:NotNull(message = "O tipo do documento é obrigatório")
    val documentType: String,

    @Schema(
        title = "ID do resultado",
        description = "Identificador do resultado do laboratório associado",
        example = "550e8400-e29b-41d4-a716-446655440001",
        type = "string",
        format = "uuid"
    )
    val resultId: UUID? = null,

    @Schema(
        title = "ID da amostra",
        description = "Identificador da amostra associada",
        example = "550e8400-e29b-41d4-a716-446655440002",
        type = "string",
        format = "uuid"
    )
    val sampleId: UUID? = null,

    @Schema(
        title = "ID do pet",
        description = "Identificador do pet associado",
        example = "550e8400-e29b-41d4-a716-446655440003",
        type = "string",
        format = "uuid"
    )
    val petId: UUID? = null,

    @Schema(
        title = "Nome do pet",
        description = "Nome do pet associado",
        example = "Rex",
        type = "string",
        maxLength = 255
    )
    val petName: String? = null,

    @Schema(
        title = "ID do veterinário",
        description = "Identificador do veterinário que fez o upload",
        example = "550e8400-e29b-41d4-a716-446655440004",
        type = "string",
        format = "uuid"
    )
    @field:NotNull(message = "O ID do veterinário que fez o upload é obrigatório")
    val uploadedBy: UUID,

    @Schema(
        title = "Nome do veterinário",
        description = "Nome do veterinário que fez o upload",
        example = "Dr. João Silva",
        type = "string",
        maxLength = 255
    )
    val uploaderName: String? = null,

    @Schema(
        title = "Data do upload",
        description = "Data e hora do upload",
        example = "2025-01-15T10:30:00",
        type = "string",
        format = "date-time"
    )
    @field:NotNull(message = "A data do upload é obrigatória")
    val uploadDate: LocalDateTime,

    @Schema(
        title = "É criptografado",
        description = "Indica se o arquivo está criptografado",
        example = "false",
        type = "boolean"
    )
    val isEncrypted: Boolean = false,

    @Schema(
        title = "Descrição",
        description = "Descrição do documento",
        example = "Resultado do exame de sangue do pet Rex",
        type = "string",
        maxLength = 1000
    )
    @field:Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    val description: String? = null,

    @Schema(
        title = "Tags",
        description = "Tags do documento separadas por vírgula",
        example = "sangue, exame, 2025",
        type = "string",
        maxLength = 500
    )
    @field:Size(max = 500, message = "As tags devem ter no máximo 500 caracteres")
    val tags: String? = null
) {

    companion object {

        fun fromEntity(entity: LabDocument): LabDocumentDTO? {
            return entity.uploadedBy?.let {
                LabDocumentDTO(
                    id = entity.id,
                    fileName = entity.fileName,
                    originalFileName = entity.originalFileName,
                    fileType = entity.fileType,
                    fileSize = entity.fileSize,
                    filePath = entity.filePath,
                    documentType = entity.documentType.name,
                    resultId = entity.resultId,
                    sampleId = entity.sampleId,
                    petId = entity.petId,
                    petName = entity.pet?.nome,
                    uploadedBy = it,
                    uploaderName = entity.uploadedByVeterinarian.fullName,
                    uploadDate = entity.uploadDate,
                    isEncrypted = entity.isEncrypted,
                    description = entity.description,
                    tags = entity.tags
                )
            }
        }

        fun toEntity(
            dto: LabDocumentDTO,
            result: LabResult?,
            sample: LabSample?,
            pet: Pet,
            veterinarian: User
        ): LabDocument {
            return LabDocument(
                id = dto.id ?: UUID.randomUUID(),
                fileName = dto.fileName,
                originalFileName = dto.originalFileName,
                fileType = dto.fileType,
                fileSize = dto.fileSize,
                filePath = dto.filePath,
                documentType = edu.fatec.petwise.domain.enums.DocumentType.valueOf(dto.documentType),
                resultId = result?.id,
                sampleId = sample?.id,
                petId = pet.id,
                uploadedBy = veterinarian.id,
                uploadDate = dto.uploadDate ?: LocalDateTime.now(),
                isEncrypted = dto.isEncrypted,
                description = dto.description,
                tags = dto.tags,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                labResult = result,
                labSample = sample,
                pet = pet,
                uploadedByVeterinarian = veterinarian
            )
        }
    }

}