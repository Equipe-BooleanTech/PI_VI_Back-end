package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.LabResultDTO
import edu.fatec.petwise.application.usecase.ManageLabResultsUseCase
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/lab/results")
@Tag(name = "Resultados de Exames", description = "Endpoints para gerenciamento de resultados de exames laboratoriais")
class LabResultsController(
    @Autowired
    private val manageLabResultsUseCase: ManageLabResultsUseCase
) {

    @GetMapping("/")
    @Operation(
        summary = "Lista todos os resultados de exames",
        description = "Retorna uma lista paginada de todos os resultados de exames laboratoriais"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Lista de resultados recuperada com sucesso",
                content = [Content(schema = Schema(implementation = LabResultDTO::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Parâmetros de paginação inválidos"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor"
            )
        ]
    )
    fun getAllResults(
        @Parameter(description = "Número da página (0-based)", example = "0")
        @RequestParam(defaultValue = "0")
        @Positive(message = "Page deve ser um número positivo")
        page: Int = 0,
        
        @Parameter(description = "Tamanho da página (máximo: 100)", example = "20")
        @RequestParam(defaultValue = "20")
        @Positive(message = "Size deve ser um número positivo")
        size: Int = 20
    ): ResponseEntity<List<LabResultDTO>> {
        return try {
            // Valida se o tamanho da página não excede o limite máximo
            val validatedSize = if (size > 100) 100 else size
            
            val results = manageLabResultsUseCase.getAllResults(page, validatedSize)
            ResponseEntity.ok(results)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            throw ex
        }
    }

    @PostMapping("/")
    @Operation(
        summary = "Cria um novo resultado de exame",
        description = "Cadastra um novo resultado de exame laboratorial no sistema"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Resultado de exame criado com sucesso",
                content = [Content(schema = Schema(implementation = LabResultDTO::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Dados do resultado inválidos ou incompletos"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor"
            )
        ]
    )
    fun createResult(
        @Parameter(description = "Dados do resultado do exame")
        @Valid @RequestBody
        dto: LabResultDTO
    ): ResponseEntity<LabResultDTO> {
        return try {

            val createdResult = manageLabResultsUseCase.createResult(dto)
            ResponseEntity.status(HttpStatus.CREATED).body(createdResult)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            throw ex
        }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Requisição inválida",
            "message" to ex.message,
            "path" to "/api/lab/results"
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.NOT_FOUND.value(),
            "error" to "Recurso não encontrado",
            "message" to ex.message,
            "path" to "/api/lab/results"
        )
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Erro interno do servidor",
            "message" to "Ocorreu um erro inesperado. Tente novamente mais tarde.",
            "path" to "/api/lab/results"
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}