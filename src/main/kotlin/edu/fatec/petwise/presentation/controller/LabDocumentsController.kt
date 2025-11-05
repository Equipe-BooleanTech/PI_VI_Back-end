package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.LabDocumentDTO
import edu.fatec.petwise.application.usecase.ManageLabDocumentsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


@RestController
@RequestMapping("/api/uploads/lab-document")
@Tag(name = "Lab Documents", description = "Endpoints para gerenciamento de documentos laboratoriais")
class LabDocumentsController @Autowired constructor(
    private val manageLabDocumentsUseCase: ManageLabDocumentsUseCase
) {

    private val logger: Logger = LoggerFactory.getLogger(LabDocumentsController::class.java)

    @PostMapping(
        value = ["", "/"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @Operation(
        summary = "Upload de documento laboratorial",
        description = "Faz upload de um novo documento laboratorial para um pet específico"
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Documento enviado com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados inválidos ou arquivo vazio"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun uploadDocument(
        @Parameter(description = "Arquivo do documento a ser enviado", required = true)
        @RequestParam("file") file: MultipartFile,
        @Parameter(description = "Metadados do documento laboratorial", required = true)
        @Valid @RequestParam("dto") dto: LabDocumentDTO
    ): ResponseEntity<LabDocumentDTO> {
        logger.info("Iniciando upload de documento para petId: ${dto.petId}")
        
        return try {
            val savedDocument = manageLabDocumentsUseCase.createDocument(dto, file)
            logger.info("Documento enviado com sucesso - ID: ${savedDocument.id}")
            ResponseEntity.status(HttpStatus.CREATED).body(savedDocument)
        } catch (e: IllegalArgumentException) {
            logger.error("Erro de validação no upload: ${e.message}")
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Erro interno no upload: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping
    @Operation(
        summary = "Listar documentos laboratoriais",
        description = "Retorna lista paginada de todos os documentos laboratoriais"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Lista de documentos retornada com sucesso"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun getAllDocuments(
        @Parameter(description = "Configurações de paginação")
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<LabDocumentDTO>> {
        return try {
            val documents = manageLabDocumentsUseCase.getAllDocuments(
                page = pageable.pageNumber,
                size = pageable.pageSize
            ).let { list ->
                // Converte para Page para manter a interface
                org.springframework.data.domain.PageImpl(
                    list,
                    pageable,
                    list.size.toLong()
                )
            }
            ResponseEntity.ok(documents)
        } catch (e: Exception) {
            logger.error("Erro ao buscar documentos: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/pet/{petId}")
    @Operation(
        summary = "Buscar documentos por pet",
        description = "Retorna todos os documentos laboratoriais de um pet específico"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Documentos encontrados"),
        ApiResponse(responseCode = "400", description = "Pet ID inválido"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun getDocumentsByPetId(
        @Parameter(description = "ID do pet", required = true)
        @PathVariable petId: UUID
    ): ResponseEntity<List<LabDocumentDTO>> {
        return try {
            val documents = manageLabDocumentsUseCase.getDocumentsByPetId(petId)
            ResponseEntity.ok(documents)
        } catch (e: IllegalArgumentException) {
            logger.error("Pet ID inválido: $petId - ${e.message}")
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Erro ao buscar documentos do pet $petId: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/result/{resultId}")
    @Operation(
        summary = "Buscar documentos por resultado",
        description = "Retorna todos os documentos associados a um resultado de laboratório específico"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Documentos encontrados"),
        ApiResponse(responseCode = "400", description = "Resultado ID inválido"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun getDocumentsByResultId(
        @Parameter(description = "ID do resultado de laboratório", required = true)
        @PathVariable resultId: UUID
    ): ResponseEntity<List<LabDocumentDTO>> {
        return try {
            val documents = manageLabDocumentsUseCase.getDocumentsByResultId(resultId)
            ResponseEntity.ok(documents)
        } catch (e: IllegalArgumentException) {
            logger.error("Resultado ID inválido: $resultId - ${e.message}")
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Erro ao buscar documentos do resultado $resultId: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar documento",
        description = "Atualiza as informações de um documento laboratorial existente"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso"),
        ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        ApiResponse(responseCode = "400", description = "Dados inválidos"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun updateDocument(
        @Parameter(description = "ID do documento", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Novos dados do documento", required = true)
        @Valid @RequestBody dto: LabDocumentDTO
    ): ResponseEntity<LabDocumentDTO> {
        return try {
            val updated = manageLabDocumentsUseCase.updateDocument(id, dto)
            ResponseEntity.ok(updated)
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            logger.error("Documento não encontrado: $id")
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Dados inválidos para documento $id: ${e.message}")
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Erro ao atualizar documento $id: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Remover documento",
        description = "Remove um documento laboratorial do sistema"
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Documento removido com sucesso"),
        ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun deleteDocument(
        @Parameter(description = "ID do documento", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        return try {
            val deleted = manageLabDocumentsUseCase.deleteDocument(id)
            if (deleted) {
                ResponseEntity.noContent().build()
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            logger.error("Documento não encontrado: $id")
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Erro ao remover documento $id: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/type/{documentType}")
    @Operation(
        summary = "Buscar documentos por tipo",
        description = "Retorna todos os documentos de um tipo específico"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Documentos encontrados"),
        ApiResponse(responseCode = "400", description = "Tipo de documento inválido"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun getDocumentsByType(
        @Parameter(description = "Tipo do documento", required = true)
        @PathVariable documentType: String
    ): ResponseEntity<List<LabDocumentDTO>> {
        return try {
            val documents = manageLabDocumentsUseCase.getDocumentsByType(documentType)
            ResponseEntity.ok(documents)
        } catch (e: IllegalArgumentException) {
            logger.error("Tipo de documento inválido: $documentType - ${e.message}")
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Erro ao buscar documentos do tipo $documentType: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/filename/{fileName}")
    @Operation(
        summary = "Buscar documento por nome do arquivo",
        description = "Busca um documento específico pelo nome do arquivo"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Documento encontrado"),
        ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun getDocumentByFileName(
        @Parameter(description = "Nome do arquivo", required = true)
        @PathVariable fileName: String
    ): ResponseEntity<LabDocumentDTO> {
        return try {
            val document = manageLabDocumentsUseCase.getDocumentByFileName(fileName)
            document.map { ResponseEntity.ok(it) }
                .orElseGet { ResponseEntity.notFound().build() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar documento pelo nome '$fileName': ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/search")
    @Operation(
        summary = "Buscar documentos",
        description = "Busca documentos por query de texto e tags opcionais"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
        ApiResponse(responseCode = "400", description = "Query inválida"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun searchDocuments(
        @Parameter(description = "Query de busca no nome e descrição", required = true)
        @RequestParam query: String,
        @Parameter(description = "Tags separadas por vírgula (opcional)")
        @RequestParam(required = false) tags: String?
    ): ResponseEntity<List<LabDocumentDTO>> {
        return try {
            val documents = manageLabDocumentsUseCase.searchDocuments(query, tags)
            ResponseEntity.ok(documents)
        } catch (e: IllegalArgumentException) {
            logger.error("Query inválida: '$query' - ${e.message}")
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("Erro na busca por '$query': ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/{id}/download")
    @Operation(
        summary = "Download de documento",
        description = "Faz download de um documento específico pelo ID"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Download realizado com sucesso"),
        ApiResponse(responseCode = "404", description = "Documento não encontrado"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun downloadDocument(
        @Parameter(description = "ID do documento", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<org.springframework.core.io.ByteArrayResource> {
        return try {
            val response = manageLabDocumentsUseCase.downloadDocument(id)
            logger.info("Download realizado com sucesso para documento: $id")
            response
        } catch (e: jakarta.persistence.EntityNotFoundException) {
            logger.error("Documento não encontrado para download: $id")
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Erro no download do documento $id: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/statistics")
    @Operation(
        summary = "Estatísticas de documentos",
        description = "Retorna estatísticas gerais dos documentos laboratoriais"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun getDocumentStatistics(): ResponseEntity<Map<String, Any>> {
        return try {
            val stats = manageLabDocumentsUseCase.getDocumentStatistics()
            logger.info("Estatísticas geradas com sucesso")
            ResponseEntity.ok(stats)
        } catch (e: Exception) {
            logger.error("Erro ao gerar estatísticas: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<String> {
        logger.error("IllegalArgumentException: ${e.message}")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Erro de validação: ${e.message}")
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException::class)
    fun handleEntityNotFoundException(e: jakarta.persistence.EntityNotFoundException): ResponseEntity<String> {
        logger.error("EntityNotFoundException: ${e.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body("Recurso não encontrado: ${e.message}")
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<String> {
        logger.error("Exception: ${e.message}", e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erro interno do servidor: ${e.message}")
    }
}