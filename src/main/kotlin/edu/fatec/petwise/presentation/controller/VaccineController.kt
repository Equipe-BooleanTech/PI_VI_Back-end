package edu.fatec.petwise.presentation.controller


import edu.fatec.petwise.application.dto.VaccineRequest
import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.application.dto.VaccineTypeResponse
import edu.fatec.petwise.application.usecase.CreateVaccineUseCase
import edu.fatec.petwise.application.usecase.ListVaccineTypesUseCase
import edu.fatec.petwise.application.usecase.ListVaccinesUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/vaccines")
@CrossOrigin(origins = ["*"])
class VaccineController(
    private val createVaccineUseCase: CreateVaccineUseCase,
    private val listVaccinesUseCase: ListVaccinesUseCase,
    private val listVaccineTypesUseCase: ListVaccineTypesUseCase
) {
    

    @GetMapping
    fun listVaccines(
        authentication: Authentication,
        @RequestParam(required = false) petId: UUID?,
        @RequestParam(defaultValue = "false") validOnly: Boolean
    ): ResponseEntity<List<VaccineResponse>> {
        val vaccines = listVaccinesUseCase.execute(authentication, petId, validOnly)
        return ResponseEntity.ok(vaccines)
    }

    @PostMapping
    fun createVaccine(
        @Valid @RequestBody request: VaccineRequest,
        @RequestParam petId: UUID,
        authentication: Authentication
    ): ResponseEntity<VaccineResponse> {
        val vaccine = createVaccineUseCase.execute(
            request = request,
            authentication = authentication,
            petId = petId
        )
        return ResponseEntity.ok(vaccine)
    }
    

    @GetMapping("/types")
    fun listVaccineTypes(
        authentication: Authentication,
        @RequestParam(required = false) species: String?
    ): ResponseEntity<List<VaccineTypeResponse>> {
        val vaccineTypes = listVaccineTypesUseCase.execute(authentication, species)
        return ResponseEntity.ok(vaccineTypes)
    }
}