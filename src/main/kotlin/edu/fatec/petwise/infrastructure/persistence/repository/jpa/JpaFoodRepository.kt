package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.FoodEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaFoodRepository : JpaRepository<FoodEntity, UUID> {
    fun findByUserId(userId: UUID): List<FoodEntity>
}
