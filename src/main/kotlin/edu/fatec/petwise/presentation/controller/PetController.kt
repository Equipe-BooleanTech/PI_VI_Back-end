package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.application.usecase.*
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/pets")
class PetController(
    private val createPetUseCase: CreatePetUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val getAllPetsUseCase: GetAllPetsUseCase,
    private val getPetsByTutorUseCase: GetPetsByTutorUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deactivatePetUseCase: DeactivatePetUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun create(@Valid @RequestBody request: CreatePetRequest): ResponseEntity<PetResponse> {
        logger.info("Requisição para criar pet recebida")
        val response = createPetUseCase.execute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<PetResponse> {
        logger.info("Requisição para buscar pet por ID: $id")
        val response = getPetByIdUseCase.execute(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<PetResponse>> {
        logger.info("Requisição para buscar todos os pets")
        val response = getAllPetsUseCase.execute()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/tutor/{tutorId}")
    fun getByTutor(@PathVariable tutorId: UUID): ResponseEntity<List<PetResponse>> {
        logger.info("Requisição para buscar pets do tutor: $tutorId")
        val response = getPetsByTutorUseCase.execute(tutorId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePetRequest
    ): ResponseEntity<PetResponse> {
        logger.info("Requisição para atualizar pet ID: $id")
        val response = updatePetUseCase.execute(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deactivate(@PathVariable id: UUID): ResponseEntity<PetResponse> {
        logger.info("Requisição para desativar pet ID: $id")
        val response = deactivatePetUseCase.execute(id)
        return ResponseEntity.ok(response)
    }
}
