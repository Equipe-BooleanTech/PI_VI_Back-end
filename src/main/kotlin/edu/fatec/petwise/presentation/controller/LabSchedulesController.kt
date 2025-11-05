package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.usecase.ManageLabSchedulesUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lab/schedules")
@Tag(name = "LabSchedules", description = "Controller para gerenciamento de agendamentos de coletas de exames laboratoriais")
class LabSchedulesController @Autowired constructor(
    private val manageLabSchedulesUseCase: ManageLabSchedulesUseCase
) {

    @GetMapping
    @Operation(
        summary = "Listar agendamentos de coletas",
        description = "Retorna uma lista paginada de todos os agendamentos de coletas de exames laboratoriais"
    )
    fun getAllSchedules(
        pageable: Pageable
    ): ResponseEntity<Page<Any>> {
        return try {
            val schedules = manageLabSchedulesUseCase.getAllSchedules(pageable)
            ResponseEntity.ok(schedules)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping
    @Operation(
        summary = "Agendar coleta de exame",
        description = "Cria um novo agendamento para coleta de exame laboratorial"
    )
    fun createSchedule(
        @RequestBody dto: Any
    ): ResponseEntity<Any> {
        return try {
            val schedule = manageLabSchedulesUseCase.createSchedule(dto)
            ResponseEntity.status(HttpStatus.CREATED).body(schedule)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Dados inválidos"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "Erro interno do servidor"))
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "Erro interno do servidor: ${ex.message}"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to "Dados inválidos: ${ex.message}"))
    }
}