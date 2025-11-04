package edu.fatec.petwise.presentation.controller


import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.usecase.GetVetScheduleUseCase
import edu.fatec.petwise.application.usecase.ListVetPatientsUseCase
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/vet")
@CrossOrigin(origins = ["*"])
class VetController(
    private val listVetPatientsUseCase: ListVetPatientsUseCase,
    private val getVetScheduleUseCase: GetVetScheduleUseCase
) {

    @GetMapping("/patients")
    fun listPatients(authentication: Authentication): ResponseEntity<List<PetResponse>> {
        val patients = listVetPatientsUseCase.execute(authentication)
        return ResponseEntity.ok(patients)
    }

    @GetMapping("/schedule")
    fun getSchedule(
        authentication: Authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<List<AppointmentResponse>> {
        val schedule = getVetScheduleUseCase.execute(authentication, startDate, endDate)
        return ResponseEntity.ok(schedule)
    }
}