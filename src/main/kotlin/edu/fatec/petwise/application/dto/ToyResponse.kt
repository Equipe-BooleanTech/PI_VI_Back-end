package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Toy
import java.time.LocalDateTime
import java.util.UUID

data class ToyResponse(
    val id: UUID?,
    val name: String,
    val brand: String,
    val category: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val unit: String,
    val material: String?,
    val ageRecommendation: String?,
    val imageUrl: String?,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(toy: Toy): ToyResponse {
            return ToyResponse(
                id = toy.id,
                name = toy.name,
                brand = toy.brand,
                category = toy.category,
                description = toy.description,
                price = toy.price,
                stock = toy.stock,
                unit = toy.unit,
                material = toy.material,
                ageRecommendation = toy.ageRecommendation,
                imageUrl = toy.imageUrl,
                active = toy.active,
                createdAt = toy.createdAt,
                updatedAt = toy.updatedAt
            )
        }
    }
}
