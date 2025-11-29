package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.PetTag
import edu.fatec.petwise.domain.repository.PetTagRepository
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaPetTagRepository
import edu.fatec.petwise.infrastructure.persistence.entity.PetTagEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PetTagRepositoryAdapter(
    private val repository: JpaPetTagRepository
) : PetTagRepository {

    override fun findByTagUid(tagUid: String): PetTag? = repository.findByTagUid(tagUid)?.toDomain()

    override fun save(petTag: PetTag): PetTag = repository.save(petTag.toEntity()).toDomain()

    override fun findByPetId(petId: UUID): List<PetTag> = repository.findByPetId(petId).map { it.toDomain() }

    override fun findActiveTags(): List<PetTag> = repository.findByActiveTrue().map { it.toDomain() }

    override fun existsByTagUid(tagUid: String): Boolean = repository.existsByTagUid(tagUid)

    override fun deleteByPetId(petId: UUID) {
        val tags = repository.findByPetId(petId)
        tags.forEach { repository.delete(it) }
    }

    private fun PetTagEntity.toDomain(): PetTag = PetTag(
        id = this.id,
        tagUid = this.tagUid,
        petId = this.petId,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun PetTag.toEntity(): PetTagEntity = PetTagEntity(
        tagUid = this.tagUid,
        petId = this.petId,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    ).apply { id = this@toEntity.id }
}