package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.CreateTutorRequest
import edu.fatec.petwise.application.dto.TutorResponse
import edu.fatec.petwise.application.dto.UpdateTutorRequest
import edu.fatec.petwise.application.usecase.*
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/tutor")
class TutorController(
    private val createTutorUseCase: CreateTutorUseCase,
    private val getTutorByIdUseCase: GetTutorByIdUseCase,
    private val getAllTutorsUseCase: GetAllTutorsUseCase,
    private val updateTutorUseCase: UpdateTutorUseCase,
    private val deactivateTutorUseCase: DeactivateTutorUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun create(@Valid @RequestBody request: CreateTutorRequest): ResponseEntity<TutorResponse> {
        logger.info("Requisição para criar tutor recebida")
        val response = createTutorUseCase.execute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<TutorResponse> {
        logger.info("Requisição para buscar tutor por ID: $id")
        val response = getTutorByIdUseCase.execute(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<TutorResponse>> {
        logger.info("Requisição para buscar todos os tutores")
        val response = getAllTutorsUseCase.execute()
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTutorRequest
    ): ResponseEntity<TutorResponse> {
        logger.info("Requisição para atualizar tutor ID: $id")
        val response = updateTutorUseCase.execute(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deactivate(@PathVariable id: UUID): ResponseEntity<TutorResponse> {
        logger.info("Requisição para desativar tutor ID: $id")
        val response = deactivateTutorUseCase.execute(id)
        return ResponseEntity.ok(response)
    }
}
