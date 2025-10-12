package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.CreateMedicationRequest
import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.application.dto.UpdateMedicationRequest
import edu.fatec.petwise.application.usecase.*
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/medications")
class MedicationController(
    private val createMedicationUseCase: CreateMedicationUseCase,
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase,
    private val getMedicationsByPetUseCase: GetMedicationsByPetUseCase,
    private val getActiveMedicationsByPetUseCase: GetActiveMedicationsByPetUseCase,
    private val updateMedicationUseCase: UpdateMedicationUseCase,
    private val deactivateMedicationUseCase: DeactivateMedicationUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateMedicationRequest,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para criar medicação recebida")
        val response = createMedicationUseCase.execute(request, ownerId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar medicação por ID: $id")
        val response = getMedicationByIdUseCase.execute(id, ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pet/{petId}")
    fun getByPet(
        @PathVariable petId: UUID,
        authentication: Authentication
    ): ResponseEntity<List<MedicationResponse>> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar medicações do pet: $petId")
        val response = getMedicationsByPetUseCase.execute(petId, ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pet/{petId}/active")
    fun getActiveByPet(
        @PathVariable petId: UUID,
        authentication: Authentication
    ): ResponseEntity<List<MedicationResponse>> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar medicações ativas do pet: $petId")
        val response = getActiveMedicationsByPetUseCase.execute(petId, ownerId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateMedicationRequest,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para atualizar medicação ID: $id")
        val response = updateMedicationUseCase.execute(id, request, ownerId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deactivate(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para desativar medicação ID: $id")
        val response = deactivateMedicationUseCase.execute(id, ownerId)
        return ResponseEntity.ok(response)
    }
}
