package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

class Toy(
    var id: UUID? = null,
    var userId: UUID,
    var name: String,
    var brand: String,
    var category: String,
    var description: String?,
    var price: Double,
    var stock: Int,
    var unit: String,
    var material: String?,
    var ageRecommendation: String?,
    var imageUrl: String?,
    var active: Boolean,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
