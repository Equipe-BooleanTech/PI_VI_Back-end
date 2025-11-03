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
import java.util.UUID

@Service
class UploadDocumentUseCase(
    private val documentRepository: DocumentRepository,
    private val petRepository: PetRepository
) {
    private val uploadDirectory = "uploads/medical-documents"
    
    init {
        // Criar diretório de upload se não existir
        Files.createDirectories(Paths.get(uploadDirectory))
    }
    
    fun execute(file: MultipartFile, documentType: String, authentication: Authentication, 
                description: String?, petId: UUID?, medicalRecordId: UUID?, appointmentId: UUID?): DocumentUploadResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        
        // Validar arquivo
        if (file.isEmpty) {
            throw IllegalArgumentException("Arquivo não pode estar vazio")
        }
        
        // Verificar tamanho máximo (5MB)
        val maxSize = 5 * 1024 * 1024
        if (file.size > maxSize) {
            throw IllegalArgumentException("Arquivo muito grande. Tamanho máximo: 5MB")
        }
        
        // Validar pet se fornecido
        var finalPetId: UUID? = null
        if (petId != null) {
            val pet = petRepository.findByIdAndOwnerId(petId, userId)
                ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")
            finalPetId = petId
        }
        
        // Gerar nome único para o arquivo
        val originalFilename = file.originalFilename ?: "document"
        val fileExtension = originalFilename.substringAfterLast(".")
        val uniqueFilename = "${UUID.randomUUID()}.$fileExtension"
        val filePath = "$uploadDirectory/$uniqueFilename"
        
        // Salvar arquivo
        val fileBytes = file.bytes
        Files.write(Paths.get(filePath), fileBytes)
        
        // Criar registro do documento
        val document = finalPetId?.let {
            Document(
                userId = userId,
                petId = it, // Será zero se não fornecido, mas o campo não é nullable
                medicalRecordId = medicalRecordId,
                appointmentId = appointmentId,
                documentName = originalFilename,
                documentType = documentType,
                filePath = filePath,
                fileSizeBytes = file.size,
                mimeType = file.contentType,
                uploadDate = LocalDateTime.now(),
                description = description,
                id = it,
                active = true,
                createdAt = TODO(),
                updatedAt = TODO()
            )
        }
        
        val savedDocument = document?.let { documentRepository.save(it) }
        return savedDocument.toDocumentUploadResponse()
    }
}

// Extension function para Document
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