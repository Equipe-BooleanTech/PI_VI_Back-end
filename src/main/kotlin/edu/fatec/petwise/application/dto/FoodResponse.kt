package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Food
import java.time.LocalDateTime
import java.util.UUID

data class FoodResponse(
    val id: UUID?,
    val name: String,
    val brand: String,
    val category: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val unit: String,
    val expiryDate: LocalDateTime?,
    val imageUrl: String?,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(food: Food): FoodResponse {
            return FoodResponse(
                id = food.id,
                name = food.name,
                brand = food.brand,
                category = food.category,
                description = food.description,
                price = food.price,
                stock = food.stock,
                unit = food.unit,
                expiryDate = food.expiryDate,
                imageUrl = food.imageUrl,
                active = food.active,
                createdAt = food.createdAt,
                updatedAt = food.updatedAt
            )
        }
    }
}
