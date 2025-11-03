package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.AppointmentListResponse
import edu.fatec.petwise.application.dto.CancelAppointmentRequest
import edu.fatec.petwise.application.dto.CancelAppointmentResponse
import edu.fatec.petwise.application.dto.CreateAppointmentRequest
import edu.fatec.petwise.application.dto.UpdateAppointmentRequest
import edu.fatec.petwise.application.dto.UpdateAppointmentStatusRequest
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
    private val getUpcomingAppointmentsByOwnerUseCase: GetUpcomingAppointmentsByOwnerUseCase,
    private val getAppointmentsByPetUseCase: GetAppointmentsByPetUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val updateAppointmentStatusUseCase: UpdateAppointmentStatusUseCase,
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
    fun getByOwner(
        authentication: Authentication,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int
    ): ResponseEntity<AppointmentListResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar consultas do tutor: $ownerId")
        val appointments = getAppointmentsByOwnerUseCase.execute(ownerId)
        val total = getAppointmentsByOwnerUseCase.count(ownerId)
        val response = AppointmentListResponse(
            consultas = appointments,
            total = total,
            page = page,
            pageSize = pageSize
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/upcoming")
    fun getUpcoming(
        authentication: Authentication,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int
    ): ResponseEntity<AppointmentListResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar consultas futuras do tutor: $ownerId")
        val appointments = getUpcomingAppointmentsByOwnerUseCase.execute(ownerId)
        val total = getUpcomingAppointmentsByOwnerUseCase.count(ownerId)
        val response = AppointmentListResponse(
            consultas = appointments,
            total = total,
            page = page,
            pageSize = pageSize
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/pet/{petId}")
    fun getByPet(
        @PathVariable petId: UUID,
        authentication: Authentication,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int
    ): ResponseEntity<AppointmentListResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para buscar consultas do pet: $petId")
        val appointments = getAppointmentsByPetUseCase.execute(petId, ownerId)
        val total = getAppointmentsByPetUseCase.count(petId, ownerId)
        val response = AppointmentListResponse(
            consultas = appointments,
            total = total,
            page = page,
            pageSize = pageSize
        )
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

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAppointmentStatusRequest,
        authentication: Authentication
    ): ResponseEntity<AppointmentResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para atualizar status da consulta ID: $id")
        val response = updateAppointmentStatusUseCase.execute(id, request.status, ownerId, request.notes)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/cancel")
    fun cancelWithReason(
        @PathVariable id: UUID,
        @RequestBody request: CancelAppointmentRequest,
        authentication: Authentication
    ): ResponseEntity<CancelAppointmentResponse> {
        val ownerId = UUID.fromString(authentication.name)
        logger.info("Requisição para cancelar consulta ID: $id com motivo: ${request.reason}")
        val response = cancelAppointmentUseCase.execute(id, ownerId)
        return ResponseEntity.ok(
            CancelAppointmentResponse(
                consultaId = response.id,
                status = response.status,
                message = "Consulta cancelada com sucesso"
            )
        )
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
