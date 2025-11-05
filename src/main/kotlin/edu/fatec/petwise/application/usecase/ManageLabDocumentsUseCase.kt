package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.LabDocumentDTO
import edu.fatec.petwise.domain.entity.LabDocument
import edu.fatec.petwise.domain.repository.LabDocumentRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ManageLabDocumentsUseCase(
    private val labDocumentRepository: LabDocumentRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(ManageLabDocumentsUseCase::class.java)

    @Transactional(readOnly = true)
    fun getAllDocuments(page: Int = 0, size: Int = 20): List<LabDocumentDTO> {
        logger.info("Buscando todos os documentos laboratoriais - página: $page, tamanho: $size")
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val documents: Page<LabDocument> = labDocumentRepository.findAll(pageable)
            documents.content.map { LabDocumentDTO.fromEntity(it) }
        } catch (ex: Exception) {
            logger.error("Erro ao buscar todos os documentos: ${ex.message}")
            throw IllegalArgumentException("Erro ao buscar documentos: ${ex.message}")
        }
    }

    @Transactional(readOnly = true)
    fun getDocumentsByPetId(petId: UUID): List<LabDocumentDTO> {
        logger.info("Buscando documentos para petId: $petId")
        return try {
            val documents = labDocumentRepository.findByPetIdOrderByUploadDateDesc(petId)
            documents.map { LabDocumentDTO.fromEntity(it) }
        } catch (ex: Exception) {
            logger.error("Erro ao buscar documentos por petId $petId: ${ex.message}")
            throw IllegalArgumentException("Erro ao buscar documentos para o pet: ${ex.message}")
        }
    }

    @Transactional(readOnly = true)
    fun getDocumentsByResultId(resultId: UUID): List<LabDocumentDTO> {
        logger.info("Buscando documentos para resultId: $resultId")
        return try {
            val documents = labDocumentRepository.findByResultIdOrderByUploadDateDesc(resultId)
            documents.map { LabDocumentDTO.fromEntity(it) }
        } catch (ex: Exception) {
            logger.error("Erro ao buscar documentos por resultId $resultId: ${ex.message}")
            throw IllegalArgumentException("Erro ao buscar documentos para o resultado: ${ex.message}")
        }
    }

    @Transactional
    fun createDocument(dto: LabDocumentDTO, file: MultipartFile): LabDocumentDTO {
        logger.info("Criando novo documento laboratorial para petId: ${dto.petId}")
        return try {
            if (file.isEmpty) throw IllegalArgumentException("Arquivo não pode estar vazio")

            val document = dto.toEntity()
            val savedDocument = labDocumentRepository.save(document)

            logger.info("Documento criado com sucesso - ID: ${savedDocument.id}")
            LabDocumentDTO.fromEntity(savedDocument)
        } catch (ex: Exception) {
            logger.error("Erro ao criar documento: ${ex.message}")
            throw IllegalArgumentException("Erro ao criar documento: ${ex.message}")
        }
    }

    @Transactional
    fun updateDocument(id: UUID, dto: LabDocumentDTO): LabDocumentDTO {
        logger.info("Atualizando documento com ID: $id")
        return try {
            val existing = labDocumentRepository.findById(id)
                .orElseThrow { EntityNotFoundException("Documento não encontrado com ID: $id") }

            val updated = existing.copy(
                fileName = dto.fileName,
                filePath = dto.filePath,
                documentType = dto.documentType,
                description = dto.description,
                tags = dto.tags,
                isEncrypted = dto.isEncrypted,
                updatedAt = dto.uploadDate ?: existing.updatedAt
            )

            val saved = labDocumentRepository.save(updated)
            LabDocumentDTO.fromEntity(saved)
        } catch (ex: EntityNotFoundException) {
            logger.error("Documento não encontrado: ${ex.message}")
            throw ex
        } catch (ex: Exception) {
            logger.error("Erro ao atualizar documento $id: ${ex.message}")
            throw IllegalArgumentException("Erro ao atualizar documento: ${ex.message}")
        }
    }

    @Transactional
    fun deleteDocument(id: UUID): Boolean {
        logger.info("Deletando documento com ID: $id")
        return try {
            if (!labDocumentRepository.existsById(id)) {
                throw EntityNotFoundException("Documento não encontrado com ID: $id")
            }
            labDocumentRepository.deleteById(id)
            logger.info("Documento deletado com sucesso - ID: $id")
            true
        } catch (ex: Exception) {
            logger.error("Erro ao deletar documento $id: ${ex.message}")
            throw IllegalArgumentException("Erro ao deletar documento: ${ex.message}")
        }
    }

    @Transactional(readOnly = true)
    fun getDocumentsByType(documentType: String): List<LabDocumentDTO> {
        logger.info("Buscando documentos do tipo: $documentType")
        return try {
            val documents = labDocumentRepository.findByDocumentType(
                edu.fatec.petwise.domain.enums.DocumentType.valueOf(documentType)
            )
            documents.map { LabDocumentDTO.fromEntity(it) }
        } catch (ex: Exception) {
            logger.error("Erro ao buscar documentos do tipo $documentType: ${ex.message}")
            throw IllegalArgumentException("Erro ao buscar documentos por tipo: ${ex.message}")
        }
    }

    @Transactional(readOnly = true)
    fun searchDocuments(query: String, tags: String? = null): List<LabDocumentDTO> {
        logger.info("Buscando documentos com query: '$query' e tags: '$tags'")
        return try ({
            val results = when {
                !tags.isNullOrEmpty() -> labDocumentRepository.findByTagsContainingIgnoreCase(tags)
                else -> labDocumentRepository.findByFileNameContainingIgnoreCase(query)
            }
            results.map { LabDocumentDTO.fromEntity(it) }
        })!! catch (ex: Exception) {
            logger.error("Erro ao buscar documentos com query '$query': ${ex.message}")
            throw IllegalArgumentException("Erro ao buscar documentos: ${ex.message}")
        }
    }

    @Transactional(readOnly = true)
    fun downloadDocument(id: UUID): ResponseEntity<ByteArrayResource> {
        logger.info("Baixando documento com ID: $id")
        return try {
            val document = labDocumentRepository.findById(id)
                .orElseThrow { EntityNotFoundException("Documento não encontrado com ID: $id") }

            val fileContent = ByteArray(0) // TODO: implementar leitura real do arquivo

            val resource = ByteArrayResource(fileContent)
            ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"${document.fileName}\"")
                .body(resource)
        } catch (ex: Exception) {
            logger.error("Erro ao baixar documento $id: ${ex.message}")
            throw IllegalArgumentException("Erro ao baixar documento: ${ex.message}")
        }
    }
}
