package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.MedicationRequest
import edu.fatec.petwise.application.dto.MedicationResponse
import edu.fatec.petwise.application.usecase.CreateMedicationUseCase
import edu.fatec.petwise.application.usecase.DeleteMedicationUseCase
import edu.fatec.petwise.application.usecase.GetMedicationsByPetUseCase
import edu.fatec.petwise.application.usecase.ListMedicationsUseCase
import edu.fatec.petwise.application.usecase.UpdateMedicationUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/medications")
@CrossOrigin(origins = ["*"])
class MedicationController(
    private val createMedicationUseCase: CreateMedicationUseCase,
    private val listMedicationsUseCase: ListMedicationsUseCase,
    private val getMedicationsByPetUseCase: GetMedicationsByPetUseCase,
    private val updateMedicationUseCase: UpdateMedicationUseCase,
    private val deleteMedicationUseCase: DeleteMedicationUseCase
) {

    @GetMapping
    fun listMedications(
        authentication: Authentication,
        @RequestParam(required = false) petId: UUID?,
        @RequestParam(required = false) searchQuery: String?
    ): ResponseEntity<List<MedicationResponse>> {
        val medications = listMedicationsUseCase.execute(authentication, petId, searchQuery)
        return ResponseEntity.ok(medications)
    }

    @PostMapping
    fun createMedication(
        @Valid @RequestBody request: MedicationRequest,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val medication = createMedicationUseCase.execute(request, authentication)
        return ResponseEntity.ok(medication)
    }


    @GetMapping("/{id}")
    fun getMedicationDetails(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val medications = listMedicationsUseCase.execute(authentication, null, null)
        val medication = medications.firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Medicação não encontrada")

        return ResponseEntity.ok(medication)
    }


    @PutMapping("/{id}")
    fun updateMedication(
        @PathVariable id: UUID,
        @Valid @RequestBody request: MedicationRequest,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val medication = updateMedicationUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(medication)
    }

    @DeleteMapping("/{id}")
    fun deleteMedication(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deleteMedicationUseCase.execute(id, authentication)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/pet/{petId}")
    fun getMedicationsByPet(
        authentication: Authentication,
        @PathVariable petId: UUID
    ): ResponseEntity<List<MedicationResponse>> {
        val medications = getMedicationsByPetUseCase.execute(authentication, petId)
        return ResponseEntity.ok(medications)
    }
}
