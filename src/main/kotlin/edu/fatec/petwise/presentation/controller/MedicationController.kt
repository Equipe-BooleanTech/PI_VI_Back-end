package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.MedicationRequest
import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.application.usecase.CreateMedicationUseCase
import edu.fatec.petwise.application.usecase.DeleteMedicationUseCase
import edu.fatec.petwise.application.usecase.ListMedicationsUseCase
import edu.fatec.petwise.application.usecase.UpdateMedicationUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/medications")
@CrossOrigin(origins = ["*"])
class MedicationController(
    private val createMedicationUseCase: CreateMedicationUseCase,
    private val listMedicationsUseCase: ListMedicationsUseCase,
    private val updateMedicationUseCase: UpdateMedicationUseCase,
    private val deleteMedicationUseCase: DeleteMedicationUseCase
) {

    @GetMapping
    fun listMedications(
        authentication: Authentication,
        @RequestParam(required = false) petId: UUID?,
        @RequestParam(required = false) searchQuery: String?
    ): ResponseEntity<List<MedicationResponse>> {
        val userId = UUID.fromString(authentication.principal.toString())
        val medications = listMedicationsUseCase.execute(userId, petId, searchQuery)
        return ResponseEntity.ok(medications)
    }

    @PostMapping
    fun createMedication(
        @Valid @RequestBody request: MedicationRequest,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val medication = createMedicationUseCase.execute(request, userId)
        return ResponseEntity.ok(medication)
    }


    @GetMapping("/{id}")
    fun getMedicationDetails(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val medication = listMedicationsUseCase.execute(userId, null, null)
            .firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Medicação não encontrada")

        return ResponseEntity.ok(medication)
    }


    @PutMapping("/{id}")
    fun updateMedication(
        @PathVariable id: UUID,
        @Valid @RequestBody request: MedicationRequest,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val medication = updateMedicationUseCase.execute(id, request)
        return ResponseEntity.ok(medication)
    }

    @DeleteMapping("/{id}")
    fun deleteMedication(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deleteMedicationUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}
