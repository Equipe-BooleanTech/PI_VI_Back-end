package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.DocumentUploadResponse
import edu.fatec.petwise.domain.entity.Document
import edu.fatec.petwise.domain.repository.DocumentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

@Service
class UploadDocumentUseCase(
    private val documentRepository: DocumentRepository,
    private val petRepository: PetRepository
) {
    private val uploadDirectory = "uploads/medical-documents"

    init {
        Files.createDirectories(Paths.get(uploadDirectory))
    }

    fun execute(
        file: MultipartFile,
        documentType: String,
        authentication: Authentication,
        description: String?,
        petId: UUID?,
        medicalRecordId: UUID?,
        appointmentId: UUID?
    ): DocumentUploadResponse {
        val userId = UUID.fromString(authentication.principal.toString())

        // Validar arquivo
        if (file.isEmpty) {
            throw IllegalArgumentException("Arquivo não pode estar vazio")
        }

        val maxSize = 5 * 1024 * 1024 // 5 MB
        if (file.size > maxSize) {
            throw IllegalArgumentException("Arquivo muito grande. Tamanho máximo: 5MB")
        }

        // Validar pet (se informado)
        val finalPetId = petId?.let {
            val pet = petRepository.findByIdAndOwnerId(it, userId)
                ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")
            pet.id
        }

        // Gerar nome único para o arquivo
        val originalFilename = file.originalFilename ?: "document"
        val fileExtension = originalFilename.substringAfterLast(".", "")
        val uniqueFilename = "${UUID.randomUUID()}.$fileExtension"
        val filePath = "$uploadDirectory/$uniqueFilename"

        // Salvar o arquivo no diretório
        Files.write(Paths.get(filePath), file.bytes)

        // Criar registro do documento
        val document = finalPetId?.let {
            Document(
                id = UUID.randomUUID(),
                userId = userId,
                petId = it,
                medicalRecordId = medicalRecordId,
                appointmentId = appointmentId,
                documentName = originalFilename,
                documentType = documentType,
                filePath = filePath,
                fileSizeBytes = file.size,
                mimeType = file.contentType,
                uploadDate = LocalDateTime.now(),
                description = description,
                active = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }

        val savedDocument = document?.let { documentRepository.save(it) }
        return savedDocument?.toDocumentUploadResponse()
            ?: throw IllegalStateException("Falha ao salvar documento")
    }
}

// Extension function para converter para DTO
private fun Document.toDocumentUploadResponse(): DocumentUploadResponse {
    return DocumentUploadResponse(
        id = id,
        petId = petId,
        medicalRecordId = medicalRecordId,
        appointmentId = appointmentId,
        documentName = documentName,
        documentType = documentType,
        filePath = filePath,
        fileSizeBytes = fileSizeBytes,
        mimeType = mimeType,
        uploadDate = uploadDate,
        description = description
    )
}
