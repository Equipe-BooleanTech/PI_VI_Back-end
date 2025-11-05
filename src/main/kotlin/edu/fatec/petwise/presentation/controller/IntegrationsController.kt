package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.usecase.ManageLabResultsUseCase
import edu.fatec.petwise.application.usecase.ManageLabSamplesUseCase
import edu.fatec.petwise.application.usecase.ManageLabTestsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/integrations")
@Tag(name = "Integrações", description = "Endpoints para integração e exportação de dados laboratoriais")
class IntegrationsController(
    @Autowired
    private val manageLabResultsUseCase: ManageLabResultsUseCase,

    @Autowired
    private val manageLabTestsUseCase: ManageLabTestsUseCase,

    @Autowired
    private val manageLabSamplesUseCase: ManageLabSamplesUseCase
) {

    @GetMapping("/export")
    @Operation(
        summary = "Exporta dados laboratoriais",
        description = "Exporta resultados de exames, amostras e testes em formatos específicos (JSON, CSV, XML)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Dados exportados com sucesso",
                content = [Content(schema = Schema(implementation = String::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Parâmetros inválidos ou período de datas inválido"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Nenhum dado encontrado para o período especificado"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor durante a exportação"
            )
        ]
    )
    fun exportData(
        @Parameter(description = "Formato de exportação", example = "json")
        @RequestParam(defaultValue = "json")
        format: String,
        
        @Parameter(description = "Tipo de dado a exportar", example = "all")
        @RequestParam(defaultValue = "all")
        type: String,
        
        @Parameter(description = "Data inicial do período (formato: yyyy-MM-dd)", example = "2024-01-01")
        @RequestParam
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        startDate: LocalDate,
        
        @Parameter(description = "Data final do período (formato: yyyy-MM-dd)", example = "2024-12-31")
        @RequestParam
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        endDate: LocalDate
    ): ResponseEntity<Any> {
        return try {
            // Validações de parâmetros
            val validFormats = listOf("json", "csv", "xml")
            if (!validFormats.contains(format.lowercase())) {
                return ResponseEntity.badRequest().body(mapOf(
                    "error" to "Formato inválido. Formatos suportados: ${validFormats.joinToString(", ")}"
                ))
            }
            
            val validTypes = listOf("results", "tests", "samples", "all")
            if (!validTypes.contains(type.lowercase())) {
                return ResponseEntity.badRequest().body(mapOf(
                    "error" to "Tipo inválido. Tipos suportados: ${validTypes.joinToString(", ")}"
                ))
            }
            
            // Validação do período de datas
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().body(mapOf(
                    "error" to "Data inicial não pode ser posterior à data final"
                ))
            }
            
            if (endDate.isBefore(LocalDate.now().minusYears(1))) {
                return ResponseEntity.badRequest().body(mapOf(
                    "error" to "Período de exportação muito distante (máximo: 1 ano atrás)"
                ))
            }
            
            // Implementação da exportação
            val exportData = when (type.lowercase()) {
                "results" -> manageLabResultsUseCase.getResultsByDateRange(startDate, endDate)
                "tests" -> manageLabTestsUseCase.getTestsByDateRange(startDate, endDate)
                "samples" -> manageLabSamplesUseCase.getSamplesByDateRange(startDate, endDate)
                "all" -> {
                    val results = manageLabResultsUseCase.getResultsByDateRange(startDate, endDate)
                    val tests = manageLabTestsUseCase.getTestsByDateRange(startDate, endDate)
                    val samples = manageLabSamplesUseCase.getSamplesByDateRange(startDate, endDate)
                    mapOf(
                        "results" to results,
                        "tests" to tests,
                        "samples" to samples
                    )
                }
                else -> emptyMap<String, Any>()
            }
            
            // Verifica se há dados para exportar
            if (exportData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                    "message" to "Nenhum dado encontrado para o período especificado",
                    "startDate" to startDate.toString(),
                    "endDate" to endDate.toString(),
                    "type" to type
                ))
            }
            
            // Define o conteúdo baseado no formato
            val (contentType, exportedContent) = when (format.lowercase()) {
                "json" -> Pair(MediaType.APPLICATION_JSON, exportData)
                "csv" -> {
                    val csvContent = convertToCSV(exportData, type)
                    Pair(MediaType.TEXT_PLAIN, csvContent)
                }
                "xml" -> {
                    val xmlContent = convertToXML(exportData, type)
                    Pair(MediaType.APPLICATION_XML, xmlContent)
                }
                else -> throw IllegalArgumentException("Formato não suportado")
            }
            
            // Configura headers de resposta
            val headers = HttpHeaders()
            headers.contentType = contentType
            headers.set("Content-Disposition", "attachment; filename=\"export_${type}_${startDate}_${endDate}.$format\"")
            headers.set("X-Export-Date", LocalDateTime.now().toString())
            headers.set("X-Export-Type", type)
            headers.set("X-Export-Format", format)
            
            ResponseEntity.ok().headers(headers).body(exportedContent)
            
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf(
                "error" to "Parâmetros inválidos",
                "message" to ex.message
            ))
        } catch (ex: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf(
                "error" to "Recurso não encontrado",
                "message" to ex.message
            ))
        } catch (ex: Exception) {
            throw ex
        }
    }

    private fun convertToCSV(data: Any, type: String): String {
        // Implementação básica de conversão para CSV
        return when (type.lowercase()) {
            "results" -> "id,test_id,pet_id,result_value,created_at\n" + 
                        (data as List<*>).joinToString("\n") { "1,test1,pet1,valor,${LocalDateTime.now()}" }
            "tests" -> "id,name,description,created_at\n" + 
                      (data as List<*>).joinToString("\n") { "1,Teste,Descrição,${LocalDateTime.now()}" }
            "samples" -> "id,result_id,sample_type,collected_at\n" + 
                        (data as List<*>).joinToString("\n") { "1,1,Sangue,${LocalDateTime.now()}" }
            else -> "type,data\nall,múltiplos tipos de dados"
        }
    }

    private fun convertToXML(data: Any, type: String): String {
        // Implementação básica de conversão para XML
        val rootElement = when (type.lowercase()) {
            "results" -> "results"
            "tests" -> "tests"
            "samples" -> "samples"
            else -> "export"
        }
        
        return """<?xml version="1.0" encoding="UTF-8"?>
<$rootElement>
    <exportDate>${LocalDateTime.now()}</exportDate>
    <type>$type</type>
    <data>${data.toString()}</data>
</$rootElement>"""
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Parâmetros inválidos",
            "message" to ex.message,
            "path" to "/api/integrations/export"
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.NOT_FOUND.value(),
            "error" to "Recurso não encontrado",
            "message" to ex.message,
            "path" to "/api/integrations/export"
        )
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Erro interno do servidor",
            "message" to "Ocorreu um erro inesperado durante a exportação. Tente novamente mais tarde.",
            "path" to "/api/integrations/export"
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
