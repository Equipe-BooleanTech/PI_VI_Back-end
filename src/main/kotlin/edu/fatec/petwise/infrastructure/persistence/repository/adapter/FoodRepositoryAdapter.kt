package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaFoodRepository
import edu.fatec.petwise.domain.entity.Food

import edu.fatec.petwise.domain.repository.FoodRepository
import edu.fatec.petwise.infrastructure.persistence.entity.FoodEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class FoodRepositoryAdapter(
    private val repository: JpaFoodRepository
) : FoodRepository {

    override fun findById(id: UUID): Optional<Food> {
        return repository.findById(id).map { it.toDomain() }
    }

    override fun save(food: Food): Food {
        val entity = food.toEntity()
        return repository.save(entity).toDomain()
    }

    override fun findByUserId(userId: UUID): List<Food> {
        return repository.findByUserId(userId).map { it.toDomain() }
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    private fun FoodEntity.toDomain(): Food {
        return Food(
            id = this.id,
            userId = this.userId,
            name = this.name,
            brand = this.brand,
            category = this.category,
            description = this.description,
            price = this.price,
            stock = this.stock,
            unit = this.unit,
            expiryDate = this.expiryDate,
            imageUrl = this.imageUrl,
            active = this.active,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun Food.toEntity(): FoodEntity {
        return FoodEntity(
            id = this.id,
            userId = this.userId,
            name = this.name,
            brand = this.brand,
            category = this.category,
            description = this.description,
            price = this.price,
            stock = this.stock,
            unit = this.unit,
            expiryDate = this.expiryDate,
            imageUrl = this.imageUrl,
            active = this.active,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
