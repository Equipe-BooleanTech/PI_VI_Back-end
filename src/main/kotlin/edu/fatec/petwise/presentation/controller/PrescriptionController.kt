package edu.fatec.petwise.presentation.controller


import edu.fatec.petwise.application.dto.PrescriptionRequest
import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.application.dto.UpdatePrescriptionRequest
import edu.fatec.petwise.application.usecase.CreatePrescriptionUseCase
import edu.fatec.petwise.application.usecase.GetPrescriptionsByPetUseCase
import edu.fatec.petwise.application.usecase.ListPrescriptionsUseCase
import edu.fatec.petwise.application.usecase.UpdatePrescriptionUseCase
import edu.fatec.petwise.application.usecase.DeletePrescriptionUseCase
import edu.fatec.petwise.application.usecase.GetPrescriptionByIdUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = ["*"])
class PrescriptionController(
    private val createPrescriptionUseCase: CreatePrescriptionUseCase,
    private val listPrescriptionsUseCase: ListPrescriptionsUseCase,
    private val getPrescriptionsByPetUseCase: GetPrescriptionsByPetUseCase,
    private val updatePrescriptionUseCase: UpdatePrescriptionUseCase,
    private val deletePrescriptionUseCase: DeletePrescriptionUseCase,
    private val getPrescriptionByIdUseCase: GetPrescriptionByIdUseCase
) {

    @GetMapping()
    fun listPrescriptions(
        authentication: Authentication,
        @RequestParam(required = false) petId: UUID?,
        @RequestParam(required = false) status: String?
    ): ResponseEntity<List<PrescriptionResponse>> {
        val prescriptions = listPrescriptionsUseCase.execute(authentication, petId, status)
        return ResponseEntity.ok(prescriptions)
    }
    

    @PostMapping()
    fun createPrescription(
        @Valid @RequestBody request: PrescriptionRequest,
        authentication: Authentication
    ): ResponseEntity<PrescriptionResponse> {
        val prescription = createPrescriptionUseCase.execute(request, authentication)
        return ResponseEntity.ok(prescription)
    }

    @GetMapping("/pet/{petId}")
    fun getPrescriptionsByPet(
        authentication: Authentication,
        @PathVariable petId: UUID
    ): ResponseEntity<List<PrescriptionResponse>> {
        val prescriptions = getPrescriptionsByPetUseCase.execute(authentication, petId)
        return ResponseEntity.ok(prescriptions)
    }

    @GetMapping("/{id}")
    fun getPrescriptionById(
        authentication: Authentication,
        @PathVariable id: UUID
    ): ResponseEntity<PrescriptionResponse> {
        val prescription = getPrescriptionByIdUseCase.execute(authentication, id)
        return ResponseEntity.ok(prescription)
    }

    @PutMapping("/{id}")
    fun updatePrescription(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePrescriptionRequest,
        authentication: Authentication
    ): ResponseEntity<PrescriptionResponse> {
        val prescription = updatePrescriptionUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(prescription)
    }

    @DeleteMapping("/{id}")
    fun deletePrescription(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deletePrescriptionUseCase.execute(id, authentication)
        return ResponseEntity.noContent().build()
    }
}
