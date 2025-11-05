package edu.fatec.petwise.presentation.controller


import com.petwise.dto.MedicationResponse
import edu.fatec.petwise.application.dto.MedicationRequest
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
        @RequestParam(required = false) administered: Boolean?
    ): ResponseEntity<List<MedicationResponse>> {
        val medications = listMedicationsUseCase.execute(authentication, petId, administered)
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
        val medication = listMedicationsUseCase.execute(authentication, null, null)
            .firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Medicação não encontrada")
        
        return ResponseEntity.ok(medication)
    }
    

    @PutMapping("/{id}")
    fun updateMedication(
        @PathVariable id: UUID,
        @RequestParam(required = false) administered: Boolean?,
        @RequestParam(required = false) administrationNotes: String?,
        authentication: Authentication
    ): ResponseEntity<MedicationResponse> {
        val medication = updateMedicationUseCase.execute(id, authentication, administered, administrationNotes)
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
}