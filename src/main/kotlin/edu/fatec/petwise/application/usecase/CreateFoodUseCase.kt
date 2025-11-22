package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.FoodRequest
import edu.fatec.petwise.application.dto.FoodResponse
import edu.fatec.petwise.domain.entity.Food
import edu.fatec.petwise.domain.repository.FoodRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class CreateFoodUseCase(
    private val foodRepository: FoodRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun execute(userId: UUID, request: FoodRequest): FoodResponse {
        val existingFood = foodRepository.findByUserId(userId)
            .firstOrNull { it.name.equals(request.name, ignoreCase = true) && it.brand.equals(request.brand, ignoreCase = true) }

        if (existingFood != null) {
            throw IllegalArgumentException("Alimento já cadastrado com este nome e marca")
        }

        val now = LocalDateTime.now()
        val food = Food(
            id = null,
            userId = userId,
            name = request.name,
            brand = request.brand,
            category = request.category,
            description = request.description,
            price = request.price,
            stock = request.stock,
            unit = request.unit,
            expiryDate = request.expiryDate,
            imageUrl = request.imageUrl,
            active = request.active,
            createdAt = now,
            updatedAt = now
        )

        val savedFood = foodRepository.save(food)

        logger.info("Alimento criado: ID=${savedFood.id}, Nome=${savedFood.name}")

        return FoodResponse.fromEntity(savedFood)
    }
}
