package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

data class DocumentUploadRequest(
    @field:NotNull(message = "Arquivo é obrigatório")
    val file: MultipartFile,
    
    @field:NotNull(message = "Tipo de documento é obrigatório")
    @field:NotBlank(message = "Tipo de documento não pode estar em branco")
    val documentType: String,
    
    val description: String? = null,
    val petId: UUID? = null,
    val medicalRecordId: Long? = null,
    val appointmentId: Long? = null
)

data class DocumentUploadResponse(
    val id: UUID,
    val petId: UUID? = null,
    val medicalRecordId: UUID?,
    val appointmentId: UUID?,
    val documentName: String,
    val documentType: String,
    val filePath: String,
    val fileSizeBytes: Long? = null,
    val mimeType: String? = null,
    val uploadDate: java.time.LocalDateTime,
    val description: String? = null
)