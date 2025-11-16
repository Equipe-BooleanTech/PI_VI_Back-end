package edu.fatec.petwise.presentation.controller


import edu.fatec.petwise.application.dto.PrescriptionRequest
import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.application.usecase.CreatePrescriptionUseCase
import edu.fatec.petwise.application.usecase.ListPrescriptionsUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/vet")
@CrossOrigin(origins = ["*"])
class PrescriptionController(
    private val createPrescriptionUseCase: CreatePrescriptionUseCase,
    private val listPrescriptionsUseCase: ListPrescriptionsUseCase
) {

    @GetMapping("/prescriptions")
    fun listPrescriptions(
        authentication: Authentication,
        @RequestParam(required = false) petId: UUID?,
        @RequestParam(required = false) status: String?
    ): ResponseEntity<List<PrescriptionResponse>> {
        val prescriptions = listPrescriptionsUseCase.execute(authentication, petId, status)
        return ResponseEntity.ok(prescriptions)
    }
    

    @PostMapping("/prescriptions")
    fun createPrescription(
        @Valid @RequestBody request: PrescriptionRequest,
        authentication: Authentication
    ): ResponseEntity<PrescriptionResponse> {
        val prescription = createPrescriptionUseCase.execute(request, authentication)
        return ResponseEntity.ok(prescription)
    }
}
