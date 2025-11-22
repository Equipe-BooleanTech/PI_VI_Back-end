package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Food
import java.util.Optional
import java.util.UUID

interface FoodRepository {
    fun findById(id: UUID): Optional<Food>
    fun save(food: Food): Food
    fun findByUserId(userId: UUID): List<Food>
    fun deleteById(id: UUID)
}
