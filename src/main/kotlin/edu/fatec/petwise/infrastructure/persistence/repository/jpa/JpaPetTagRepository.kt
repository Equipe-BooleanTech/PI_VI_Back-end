package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.PetTagEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaPetTagRepository: JpaRepository<PetTagEntity, UUID> {
    fun findByTagUid(tagUid: String): PetTagEntity?
    fun findByPetId(petId: UUID): List<PetTagEntity>
    fun findByActiveTrue(): List<PetTagEntity>
    fun existsByTagUid(tagUid: String): Boolean
}