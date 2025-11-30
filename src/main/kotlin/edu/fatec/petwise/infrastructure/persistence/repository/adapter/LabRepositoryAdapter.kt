package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.Lab
import edu.fatec.petwise.domain.repository.LabRepository
import edu.fatec.petwise.infrastructure.persistence.entity.LabEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaLabRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class LabRepositoryAdapter(
    private val repository: JpaLabRepository
) : LabRepository {

    override fun findAll(): List<Lab> {
        return repository.findAll().map { it.toDomain() }
    }

    override fun finByVeterinaryId(veterinaryId: UUID): List<Lab> {
        
        return emptyList()
    }

    override fun findById(id: UUID): Optional<Lab> {
        return repository.findById(id).map { it.toDomain() }
    }

    override fun save(lab: Lab): Lab {
        val entity = lab.toEntity()
        return repository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    private fun LabEntity.toDomain(): Lab {
        return Lab(
            id = this.id,
            name = this.name,
            contactInfo = this.contactInfo,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun Lab.toEntity(): LabEntity {
        return LabEntity(
            id = this.id,
            name = this.name,
            contactInfo = this.contactInfo,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
