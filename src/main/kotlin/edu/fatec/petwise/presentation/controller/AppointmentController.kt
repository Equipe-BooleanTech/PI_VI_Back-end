package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.CompleteAppointmentRequest
import edu.fatec.petwise.application.dto.CreateAppointmentRequest
import edu.fatec.petwise.application.dto.UpdateAppointmentRequest
import edu.fatec.petwise.application.usecase.*
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/appointments")
class AppointmentController(
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase,
    private val getAppointmentsByOwnerUseCase: GetAppointmentsByOwnerUseCase,
    private val getAppointmentsByPetUseCase: GetAppointmentsByPetUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateAppointmentRequest,
        authentication: Authentication
    ): ResponseEntity<AppointmentResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para criar consulta recebida")
        val response = createAppointmentUseCase.execute(request, ownerId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<AppointmentResponse> {
        val userId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar consulta por ID: $id")
        val response = getAppointmentByIdUseCase.execute(id, userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getByOwner(authentication: Authentication): ResponseEntity<List<AppointmentResponse>> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar consultas do tutor: $ownerId")
        val response = getAppointmentsByOwnerUseCase.execute(ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pet/{petId}")
    fun getByPet(
        @PathVariable petId: UUID,
        authentication: Authentication
    ): ResponseEntity<List<AppointmentResponse>> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar consultas do pet: $petId")
        val response = getAppointmentsByPetUseCase.execute(petId, ownerId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAppointmentRequest,
        authentication: Authentication
    ): ResponseEntity<AppointmentResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para atualizar consulta ID: $id")
        val response = updateAppointmentUseCase.execute(id, request, ownerId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun cancel(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<AppointmentResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para cancelar consulta ID: $id")
        val response = cancelAppointmentUseCase.execute(id, ownerId)
        return ResponseEntity.ok(response)
    }
}
