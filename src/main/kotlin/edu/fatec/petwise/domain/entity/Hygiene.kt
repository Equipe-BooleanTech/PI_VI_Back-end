package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

class Hygiene(
    var id: UUID? = null,
    var name: String,
    var brand: String,
    var category: String,
    var description: String?,
    var price: Double,
    var stock: Int,
    var unit: String,
    var expiryDate: LocalDateTime?,
    var imageUrl: String?,
    var active: Boolean,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
