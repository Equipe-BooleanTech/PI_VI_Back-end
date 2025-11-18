package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.LabRequest
import edu.fatec.petwise.application.dto.LabResponse
import edu.fatec.petwise.application.usecase.CreateLabUseCase
import edu.fatec.petwise.application.usecase.DeleteLabUseCase
import edu.fatec.petwise.application.usecase.ListLabsUseCase
import edu.fatec.petwise.application.usecase.UpdateLabUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/labs")
@CrossOrigin(origins = ["*"])
class LabController(
    private val createLabUseCase: CreateLabUseCase,
    private val listLabsUseCase: ListLabsUseCase,
    private val updateLabUseCase: UpdateLabUseCase,
    private val deleteLabUseCase: DeleteLabUseCase
) {

    @GetMapping
    fun listLabs(
        authentication: Authentication
    ): ResponseEntity<List<LabResponse>> {
        val labs = listLabsUseCase.execute(authentication)
        return ResponseEntity.ok(labs)
    }

    @PostMapping
    fun createLab(
        @Valid @RequestBody request: LabRequest,
        authentication: Authentication
    ): ResponseEntity<LabResponse> {
        val lab = createLabUseCase.execute(
            request = request,
            authentication = authentication
        )
        return ResponseEntity.ok(lab)
    }

    @PutMapping("/{id}")
    fun updateLab(
        @PathVariable id: UUID,
        @Valid @RequestBody request: LabRequest,
        authentication: Authentication
    ): ResponseEntity<LabResponse> {
        val lab = updateLabUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(lab)
    }

    @DeleteMapping("/{id}")
    fun deleteLab(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deleteLabUseCase.execute(id, authentication)
        return ResponseEntity.noContent().build()
    }
}
