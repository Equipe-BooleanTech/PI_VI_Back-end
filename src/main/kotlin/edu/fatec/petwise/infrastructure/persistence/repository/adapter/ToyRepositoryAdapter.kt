package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaToyRepository
import edu.fatec.petwise.domain.entity.Toy
import edu.fatec.petwise.domain.repository.ToyRepository
import edu.fatec.petwise.infrastructure.persistence.entity.ToyEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ToyRepositoryAdapter(
    private val repository: JpaToyRepository
) : ToyRepository {

    override fun findById(id: UUID): Optional<Toy> {
        return repository.findById(id).map { it.toDomain() }
    }

    override fun save(toy: Toy): Toy {
        val entity = toy.toEntity()
        return repository.save(entity).toDomain()
    }

    override fun findAll(): List<Toy> {
        return repository.findAll().map { it.toDomain() }
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    private fun ToyEntity.toDomain(): Toy {
        return Toy(
            id = this.id,
            name = this.name,
            brand = this.brand,
            category = this.category,
            description = this.description,
            price = this.price,
            stock = this.stock,
            unit = this.unit,
            material = this.material,
            ageRecommendation = this.ageRecommendation,
            imageUrl = this.imageUrl,
            active = this.active,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun Toy.toEntity(): ToyEntity {
        return ToyEntity(
            id = this.id,
            name = this.name,
            brand = this.brand,
            category = this.category,
            description = this.description,
            price = this.price,
            stock = this.stock,
            unit = this.unit,
            material = this.material,
            ageRecommendation = this.ageRecommendation,
            imageUrl = this.imageUrl,
            active = this.active,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
