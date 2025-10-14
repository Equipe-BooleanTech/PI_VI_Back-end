package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.application.dto.UpdateHealthStatusRequest
import edu.fatec.petwise.application.usecase.*
import edu.fatec.petwise.infrastructure.security.JwtService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/pets")
@PreAuthorize("hasRole('OWNER')")
class PetController(
    private val createPetUseCase: CreatePetUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase,
    private val getAllPetsUseCase: GetAllPetsUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val deletePetUseCase: DeletePetUseCase,
    private val toggleFavoritePetUseCase: ToggleFavoritePetUseCase,
    private val updateHealthStatusUseCase: UpdateHealthStatusUseCase,
    private val searchPetsUseCase: SearchPetsUseCase,
    private val getFavoritePetsUseCase: GetFavoritePetsUseCase,
    private val jwtService: JwtService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private fun getUserIdFromToken(request: HttpServletRequest): UUID {
        val authHeader = request.getHeader("Authorization")
        val token = authHeader?.substring(7)
        return UUID.fromString(jwtService.getUserIdFromToken(token!!))
    }

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreatePetRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<PetResponse> {
        logger.info("Requisição para criar pet recebida")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = createPetUseCase.execute(request, ownerId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<PetResponse> {
        logger.info("Requisição para buscar pet por ID: $id")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = getPetByIdUseCase.execute(id, ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAll(httpRequest: HttpServletRequest): ResponseEntity<List<PetResponse>> {
        logger.info("Requisição para buscar todos os pets")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = getAllPetsUseCase.execute(ownerId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePetRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<PetResponse> {
        logger.info("Requisição para atualizar pet ID: $id")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = updatePetUseCase.execute(id, request, ownerId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Void> {
        logger.info("Requisição para excluir pet ID: $id")
        val ownerId = getUserIdFromToken(httpRequest)
        deletePetUseCase.execute(id, ownerId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/favorite")
    fun toggleFavorite(
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): ResponseEntity<PetResponse> {
        logger.info("Requisição para alternar favorito do pet ID: $id")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = toggleFavoritePetUseCase.execute(id, ownerId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}/health-status")
    fun updateHealthStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateHealthStatusRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<PetResponse> {
        logger.info("Requisição para atualizar status de saúde do pet ID: $id")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = updateHealthStatusUseCase.execute(id, request, ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search")
    fun search(
        @RequestParam query: String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<PetResponse>> {
        logger.info("Requisição para buscar pets com query: $query")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = searchPetsUseCase.execute(query, ownerId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/favorites")
    fun getFavorites(httpRequest: HttpServletRequest): ResponseEntity<List<PetResponse>> {
        logger.info("Requisição para buscar pets favoritos")
        val ownerId = getUserIdFromToken(httpRequest)
        val response = getFavoritePetsUseCase.execute(ownerId)
        return ResponseEntity.ok(response)
    }
}
