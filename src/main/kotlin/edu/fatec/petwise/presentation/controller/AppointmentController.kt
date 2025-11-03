package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.CreateAppointmentRequest
import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.application.dto.UpdateAppointmentRequest
import edu.fatec.petwise.application.usecase.*
import edu.fatec.petwise.domain.entity.AppointmentStatus
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/appointments")
class AppointmentController(
    private val listUserAppointmentsUseCase: ListUserAppointmentsUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val getAppointmentDetailsUseCase: GetAppointmentDetailsUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) {

    @GetMapping
    fun listAppointments(
        authentication: Authentication,
        @RequestParam(required = false) status: AppointmentStatus?
    ): ResponseEntity<List<AppointmentResponse>> {
        val userId = authentication.name
        val appointments = listUserAppointmentsUseCase.execute(userId, status)
        return ResponseEntity.ok(appointments)
    }

    @PostMapping
    fun createAppointment(
        authentication: Authentication,
        @Valid @RequestBody request: CreateAppointmentRequest
    ): ResponseEntity<AppointmentResponse> {
        val userId = authentication.name
        val appointment = createAppointmentUseCase.execute(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment)
    }

    @GetMapping("/{id}")
    fun getAppointmentDetails(
        authentication: Authentication,
        @PathVariable id: String
    ): ResponseEntity<AppointmentResponse> {
        val userId = authentication.name
        val appointment = getAppointmentDetailsUseCase.execute(userId, id)
        return ResponseEntity.ok(appointment)
    }
    

    @PutMapping("/{id}")
    fun updateAppointment(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateAppointmentRequest
    ): ResponseEntity<AppointmentResponse> {
        val userId = authentication.name
        val appointment = updateAppointmentUseCase.execute(userId, id, request)
        return ResponseEntity.ok(appointment)
    }

    @DeleteMapping("/{id}")
    fun cancelAppointment(
        authentication: Authentication,
        @PathVariable id: String
    ): ResponseEntity<MessageResponse> {
        val userId = authentication.name
        val response = cancelAppointmentUseCase.execute(userId, id)
        return ResponseEntity.ok(response)
    }
}
