package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.Hygiene
import edu.fatec.petwise.domain.repository.HygieneRepository
import edu.fatec.petwise.infrastructure.persistence.entity.HygieneEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaHygieneRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class HygieneRepositoryAdapter(
    private val repository: JpaHygieneRepository
) : HygieneRepository {

    override fun findById(id: UUID): Optional<Hygiene> {
        return repository.findById(id).map { it.toDomain() }
    }

    override fun save(hygiene: Hygiene): Hygiene {
        val entity = hygiene.toEntity()
        return repository.save(entity).toDomain()
    }

    override fun findByUserId(userId: UUID): List<Hygiene> {
        return repository.findByUserId(userId).map { it.toDomain() }
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    private fun HygieneEntity.toDomain(): Hygiene {
        return Hygiene(
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

    private fun Hygiene.toEntity(): HygieneEntity {
        return HygieneEntity(
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
