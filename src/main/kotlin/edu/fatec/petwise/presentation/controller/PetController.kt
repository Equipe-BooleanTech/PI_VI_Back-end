package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.usecase.CreatePetUseCase
import edu.fatec.petwise.application.usecase.DeletePetUseCase
import edu.fatec.petwise.application.usecase.GetAllPetsUseCase
import edu.fatec.petwise.application.usecase.GetFavoritePetsUseCase
import edu.fatec.petwise.application.usecase.GetPetDetailsUseCase
import edu.fatec.petwise.application.usecase.SearchPetsUseCase
import edu.fatec.petwise.application.usecase.ToggleFavoriteUseCase
import edu.fatec.petwise.application.usecase.UpdateHealthStatusUseCase
import edu.fatec.petwise.application.usecase.UpdatePetUseCase
import edu.fatec.petwise.application.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/pets")
class PetController(
    private val getAllPetsUseCase: GetAllPetsUseCase,
    private val createPetUseCase: CreatePetUseCase,
    private val getPetDetailsUseCase: GetPetDetailsUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val updateHealthStatusUseCase: UpdateHealthStatusUseCase,
    private val searchPetsUseCase: SearchPetsUseCase,
    private val getFavoritePetsUseCase: GetFavoritePetsUseCase
) {

    @GetMapping
    fun getAllPets(
        authentication: Authentication,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int
    ): ResponseEntity<PetListResponse> {
        val userId = UUID.fromString(authentication.name)
        val result = getAllPetsUseCase.execute(userId, page, pageSize)
        return ResponseEntity.ok(result)
    }

    @PostMapping
    fun createPet(
        authentication: Authentication,
        @Valid @RequestBody request: CreatePetRequest
    ): ResponseEntity<PetResponse> {
        val userId = UUID.fromString(authentication.name)
        val pet = createPetUseCase.execute(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(pet)
    }

    @GetMapping("/{id}")
    fun getPetDetails(@PathVariable id: UUID): ResponseEntity<PetResponse> {
        val pet = getPetDetailsUseCase.execute(id)
        return ResponseEntity.ok(pet)
    }

    @PutMapping("/{id}")
    fun updatePet(
        authentication: Authentication,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePetRequest
    ): ResponseEntity<PetResponse> {
        val userId = UUID.fromString(authentication.name)
        val pet = updatePetUseCase.execute(userId, id, request)
        return ResponseEntity.ok(pet)
    }

    @DeleteMapping("/{id}")
    fun deletePet(
        authentication: Authentication,
        @PathVariable id: UUID
    ): ResponseEntity<MessageResponse> {
        val userId = UUID.fromString(authentication.name)
        val response = deletePetUseCase.execute(userId, id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/favorite")
    fun toggleFavorite(
        authentication: Authentication,
        @PathVariable id: UUID
    ): ResponseEntity<ToggleFavoriteResponse> {
        val userId = authentication.name
        val response = toggleFavoriteUseCase.execute(userId, id)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/health")
    fun updateHealthStatus(
        authentication: Authentication,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateHealthStatusRequest
    ): ResponseEntity<PetResponse> {
        val pet = updateHealthStatusUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(pet)
    }

    @GetMapping("/search")
    fun searchPets(
        authentication: Authentication,
        @RequestParam q: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int
    ): ResponseEntity<PetListResponse> {
        val userId = UUID.fromString(authentication.name)
        val result = searchPetsUseCase.execute(userId, q, page, pageSize)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/favorites")
    fun getFavoritePets(
        authentication: Authentication,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int
    ): ResponseEntity<PetListResponse> {
        val userId = UUID.fromString(authentication.name)
        val result = getFavoritePetsUseCase.execute(userId, page, pageSize)
        return ResponseEntity.ok(result)
    }
}
