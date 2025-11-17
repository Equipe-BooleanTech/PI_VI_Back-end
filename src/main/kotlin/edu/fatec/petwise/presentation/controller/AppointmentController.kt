package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.usecase.CancelAppointmentUseCase
import edu.fatec.petwise.application.usecase.CreateAppointmentUseCase
import edu.fatec.petwise.application.usecase.GetAppointmentDetailsUseCase
import edu.fatec.petwise.application.usecase.ListAppointmentsUseCase
import edu.fatec.petwise.application.usecase.UpdateAppointmentUseCase
import edu.fatec.petwise.application.dto.*
import edu.fatec.petwise.domain.enums.ConsultaStatus
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/appointments")
class AppointmentController(
    private val listAppointmentsUseCase: ListAppointmentsUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val getAppointmentDetailsUseCase: GetAppointmentDetailsUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) {

    @GetMapping
    fun listAppointments(
        @RequestParam(required = false) status: ConsultaStatus?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int
    ): ResponseEntity<AppointmentListResponse> {
        val result = listAppointmentsUseCase.execute(status, page, pageSize)
        return ResponseEntity.ok(result)
    }

    @PostMapping
    fun createAppointment(
        authentication: Authentication,
        @Valid @RequestBody request: CreateAppointmentRequest
    ): ResponseEntity<AppointmentResponse> {
        val userId = UUID.fromString(authentication.name)
        val appointment = createAppointmentUseCase.execute(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment)
    }

    @GetMapping("/{id}")
    fun getAppointmentDetails(@PathVariable id: UUID): ResponseEntity<AppointmentResponse> {
        val appointment = getAppointmentDetailsUseCase.execute(id)
        return ResponseEntity.ok(appointment)
    }

    @PutMapping("/{id}")
    fun updateAppointment(
        authentication: Authentication,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAppointmentRequest
    ): ResponseEntity<AppointmentResponse> {
        val userId = UUID.fromString(authentication.name)
        val appointment = updateAppointmentUseCase.execute(userId, id, request)
        return ResponseEntity.ok(appointment)
    }

    @DeleteMapping("/{id}")
    fun cancelAppointment(
        authentication: Authentication,
        @PathVariable id: UUID
    ): ResponseEntity<MessageResponse> {
        val userId = UUID.fromString(authentication.name)
        val response = cancelAppointmentUseCase.execute(userId, id)
        return ResponseEntity.ok(response)
    }
}
