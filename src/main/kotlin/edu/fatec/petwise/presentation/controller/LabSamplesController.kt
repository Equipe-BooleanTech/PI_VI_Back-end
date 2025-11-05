package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.LabSampleDTO
import edu.fatec.petwise.application.usecase.ManageLabSamplesUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ExceptionHandler

@RestController
@RequestMapping("/api/lab/samples")
@Tag(name = "Lab Samples", description = "Controller para gerenciamento de amostras de laboratório")
class LabSamplesController @Autowired constructor(
    private val manageLabSamplesUseCase: ManageLabSamplesUseCase
) {

    @GetMapping
    @Operation(
        summary = "Listar todas as amostras",
        description = "Retorna uma lista paginada de todas as amostras de laboratório"
    )
    fun getAllSamples(
        @Parameter(description = "Página (0-based)", example = "0")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Tamanho da página", example = "20")
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<LabSampleDto>> {
        val pageable = Pageable.ofSize(size).withPage(page)
        val samples = manageLabSamplesUseCase.getAllSamples(pageable)
        return ResponseEntity.ok(samples)
    }

    @PostMapping
    @Operation(
        summary = "Registrar nova amostra",
        description = "Cria um novo registro de amostra de laboratório"
    )
    fun createSample(
        @Parameter(description = "Dados da nova amostra")
        @RequestBody dto: LabSampleCreateDto
    ): ResponseEntity<LabSampleDTO> {
        val createdSample = manageLabSamplesUseCase.createSample(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSample)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar amostra por ID",
        description = "Retorna os detalhes de uma amostra específica"
    )
    fun getSampleById(
        @Parameter(description = "ID da amostra", example = "1")
        @PathVariable id: Long
    ): ResponseEntity<LabSampleDTO> {
        val sample = manageLabSamplesUseCase.getSampleById(id)
        return ResponseEntity.ok(sample)
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Atualizar status da amostra",
        description = "Atualiza o status de uma amostra de laboratório"
    )
    fun updateSampleStatus(
        @Parameter(description = "ID da amostra", example = "1")
        @PathVariable id: Long,
        @Parameter(description = "Novo status da amostra")
        @RequestBody statusDto: LabSampleStatusDto
    ): ResponseEntity<LabSampleDTO> {
        val updatedSample = manageLabSamplesUseCase.updateSampleStatus(id, statusDto.status)
        return ResponseEntity.ok(updatedSample)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erro interno do servidor: ${ex.message}")
    }
}