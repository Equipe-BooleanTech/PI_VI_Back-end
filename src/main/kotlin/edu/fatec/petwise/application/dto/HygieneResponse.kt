package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Hygiene
import java.time.LocalDateTime
import java.util.UUID

data class HygieneResponse(
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
        fun fromEntity(hygiene: Hygiene): HygieneResponse {
            return HygieneResponse(
                id = hygiene.id,
                name = hygiene.name,
                brand = hygiene.brand,
                category = hygiene.category,
                description = hygiene.description,
                price = hygiene.price,
                stock = hygiene.stock,
                unit = hygiene.unit,
                expiryDate = hygiene.expiryDate,
                imageUrl = hygiene.imageUrl,
                active = hygiene.active,
                createdAt = hygiene.createdAt,
                updatedAt = hygiene.updatedAt
            )
        }
    }
}
