package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.usecase.UpdateFoodUseCase
import edu.fatec.petwise.application.dto.FoodRequest
import edu.fatec.petwise.application.dto.FoodResponse
import edu.fatec.petwise.application.usecase.CreateFoodUseCase
import edu.fatec.petwise.application.usecase.DeleteFoodUseCase
import edu.fatec.petwise.application.usecase.GetFoodByIdUseCase
import edu.fatec.petwise.application.usecase.ListFoodsUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/foods")
class FoodController(
    private val createFoodUseCase: CreateFoodUseCase,
    private val listFoodsUseCase: ListFoodsUseCase,
    private val getFoodByIdUseCase: GetFoodByIdUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase
) {

    @GetMapping
    fun listFoods(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) searchQuery: String?,
        @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean,
        authentication: Authentication
    ): ResponseEntity<List<FoodResponse>> {
        val userId = UUID.fromString(authentication.principal.toString())
        val foods = listFoodsUseCase.execute(userId, category, searchQuery, activeOnly)
        return ResponseEntity.ok(foods)
    }

    @PostMapping
    fun createFood(
        @Valid @RequestBody request: FoodRequest,
        authentication: Authentication
    ): ResponseEntity<FoodResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val food = createFoodUseCase.execute(userId, request)
        return ResponseEntity.ok(food)
    }

    @GetMapping("/{id}")
    fun getFoodDetails(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<FoodResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val food = getFoodByIdUseCase.execute(userId, id)
            ?: throw IllegalArgumentException("Alimento não encontrado")

        return ResponseEntity.ok(food)
    }

    @PutMapping("/{id}")
    fun updateFood(
        @PathVariable id: UUID,
        @Valid @RequestBody request: FoodRequest,
        authentication: Authentication
    ): ResponseEntity<FoodResponse> {
        val food = updateFoodUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(food)
    }

    @DeleteMapping("/{id}")
    fun deleteFood(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deleteFoodUseCase.execute(id, authentication)
        return ResponseEntity.noContent().build()
    }
}
