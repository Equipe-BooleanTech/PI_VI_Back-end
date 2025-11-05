package edu.fatec.petwise.presentation.controller


import edu.fatec.petwise.application.dto.LabTestDTO
import edu.fatec.petwise.application.usecase.ManageLabTestsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID
import java.util.Optional

@RestController
@RequestMapping("/api/lab/tests")
@Tag(name = "Exames Laboratoriais", description = "Operações para gerenciamento de exames laboratoriais de pets")
class LabTestsController @Autowired constructor(
    private val manageLabTestsUseCase: ManageLabTestsUseCase
) {

    @GetMapping
    @Operation(
        summary = "Listar todos os exames",
        description = "Retorna uma lista paginada de todos os exames laboratoriais disponíveis no sistema"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de exames retornada com sucesso"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun getAllTests(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<LabTestDTO>> {
        return try {
            val tests = manageLabTestsUseCase.getAllTests(page, size)
            ResponseEntity.ok(tests)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping
    @Operation(
        summary = "Registrar novo exame",
        description = "Cria um novo exame laboratorial no sistema"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Exame criado com sucesso"),
            ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun createTest(@RequestBody dto: LabTestDTO): ResponseEntity<LabTestDTO> {
        return try {
            val createdTest = manageLabTestsUseCase.createTest(dto)
            ResponseEntity.status(HttpStatus.CREATED).body(createdTest)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar exame por ID",
        description = "Retorna os detalhes de um exame específico baseado no seu código"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Exame encontrado com sucesso"),
            ApiResponse(responseCode = "404", description = "Exame não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun getTestByCode(@PathVariable testCode: String): ResponseEntity<LabTestDTO> {
        return try {
            val testOpt = manageLabTestsUseCase.getTestByCode(testCode)
            if (testOpt.isPresent) {
                ResponseEntity.ok(testOpt.get())
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar exame",
        description = "Atualiza as informações de um exame existente"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Exame atualizado com sucesso"),
            ApiResponse(responseCode = "404", description = "Exame não encontrado"),
            ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun updateTest(
        @PathVariable id: UUID,
        @RequestBody dto: LabTestDTO
    ): ResponseEntity<LabTestDTO> {
        return try {
            val updatedTest = manageLabTestsUseCase.updateTest(id, dto)
            ResponseEntity.ok(updatedTest)
        } catch (ex: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Remover exame",
        description = "Remove um exame do sistema"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Exame removido com sucesso"),
            ApiResponse(responseCode = "404", description = "Exame não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun deleteTest(@PathVariable id: UUID): ResponseEntity<Void> {
        return try {
            manageLabTestsUseCase.deleteTest(id)
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        } catch (ex: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Exame não encontrado: ${ex.message}")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Dados inválidos: ${ex.message}")
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erro interno do servidor: ${ex.message}")
    }
}