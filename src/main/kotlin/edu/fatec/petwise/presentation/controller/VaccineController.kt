package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.CreateVaccineRequest
import edu.fatec.petwise.application.dto.UpdateVaccineRequest
import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.application.usecase.*
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/vaccines")
class VaccineController(
    private val createVaccineUseCase: CreateVaccineUseCase,
    private val getVaccineByIdUseCase: GetVaccineByIdUseCase,
    private val getVaccinesByPetUseCase: GetVaccinesByPetUseCase,
    private val getDueVaccinesByPetUseCase: GetDueVaccinesByPetUseCase,
    private val updateVaccineUseCase: UpdateVaccineUseCase,
    private val deleteVaccineUseCase: DeleteVaccineUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateVaccineRequest,
        authentication: Authentication
    ): ResponseEntity<VaccineResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para criar vacina recebida")
        val response = createVaccineUseCase.execute(request, ownerId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<VaccineResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar vacina por ID: $id")
        val response = getVaccineByIdUseCase.execute(id, ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pet/{petId}")
    fun getByPet(
        @PathVariable petId: UUID,
        authentication: Authentication
    ): ResponseEntity<List<VaccineResponse>> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar vacinas do pet: $petId")
        val response = getVaccinesByPetUseCase.execute(petId, ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pet/{petId}/due")
    fun getDueByPet(
        @PathVariable petId: UUID,
        authentication: Authentication
    ): ResponseEntity<List<VaccineResponse>> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar vacinas pendentes do pet: $petId")
        val response = getDueVaccinesByPetUseCase.execute(petId, ownerId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateVaccineRequest,
        authentication: Authentication
    ): ResponseEntity<VaccineResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para atualizar vacina ID: $id")
        val response = updateVaccineUseCase.execute(id, request, ownerId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para excluir vacina ID: $id")
        deleteVaccineUseCase.execute(id, ownerId)
        return ResponseEntity.noContent().build()
    }
}
