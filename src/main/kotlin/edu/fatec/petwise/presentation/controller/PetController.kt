package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.application.usecase.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pets")
class PetController(
    private val listUserPetsUseCase: ListUserPetsUseCase,
    private val createPetUseCase: CreatePetUseCase,
    private val getPetDetailsUseCase: GetPetDetailsUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase
) {

    @GetMapping
    fun listPets(
        authentication: Authentication,
        @RequestParam(defaultValue = "false") includeInactive: Boolean
    ): ResponseEntity<List<PetResponse>> {
        val userId = authentication.name
        val pets = listUserPetsUseCase.execute(userId, includeInactive)
        return ResponseEntity.ok(pets)
    }
    

    @PostMapping
    fun createPet(
        authentication: Authentication,
        @Valid @RequestBody request: CreatePetRequest
    ): ResponseEntity<PetResponse> {
        val userId = authentication.name
        val pet = createPetUseCase.execute(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(pet)
    }
    

    @GetMapping("/{id}")
    fun getPetDetails(
        authentication: Authentication,
        @PathVariable id: String
    ): ResponseEntity<PetResponse> {
        val userId = authentication.name
        val pet = getPetDetailsUseCase.execute(userId, id)
        return ResponseEntity.ok(pet)
    }
    

    @PutMapping("/{id}")
    fun updatePet(
        authentication: Authentication,
        @PathVariable id: String,
        @Valid @RequestBody request: UpdatePetRequest
    ): ResponseEntity<PetResponse> {
        val userId = authentication.name
        val pet = updatePetUseCase.execute(userId, id, request)
        return ResponseEntity.ok(pet)
    }
    

    @DeleteMapping("/{id}")
    fun deletePet(
        authentication: Authentication,
        @PathVariable id: String,
        @RequestParam(defaultValue = "false") forceDelete: Boolean
    ): ResponseEntity<MessageResponse> {
        val userId = authentication.name
        val response = deletePetUseCase.execute(userId, id, forceDelete)
        return ResponseEntity.ok(response)
    }
}
