package edu.fatec.petwise.presentation.controller


import edu.fatec.petwise.application.dto.MedicalRecordRequest
import edu.fatec.petwise.application.dto.MedicalRecordResponse
import edu.fatec.petwise.application.usecase.CreateMedicalRecordUseCase
import edu.fatec.petwise.application.usecase.ListPetMedicalHistoryUseCase
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/pets")
@CrossOrigin(origins = ["*"])
class MedicalRecordController(
    private val createMedicalRecordUseCase: CreateMedicalRecordUseCase,
    private val listPetMedicalHistoryUseCase: ListPetMedicalHistoryUseCase
) {

    @GetMapping("/{petId}/medical-records")
    fun getMedicalHistory(
        @PathVariable petId: UUID,
        authentication: Authentication,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<List<MedicalRecordResponse>> {
        val medicalHistory = listPetMedicalHistoryUseCase.execute(petId, authentication, startDate, endDate)
        return ResponseEntity.ok(medicalHistory)
    }

    @PostMapping("/vet/medical-records")
    fun createMedicalRecord(
        @Valid @RequestBody request: MedicalRecordRequest,
        authentication: Authentication
    ): ResponseEntity<MedicalRecordResponse> {
        val medicalRecord = createMedicalRecordUseCase.execute(request, authentication)
        return ResponseEntity.ok(medicalRecord)
    }
}