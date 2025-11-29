package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.PetTag
import java.util.UUID

interface PetTagRepository {
    fun findByTagUid(tagUid: String): PetTag?
    fun save(petTag: PetTag): PetTag
    fun findByPetId(petId: UUID): List<PetTag>
    fun findActiveTags(): List<PetTag>
    fun existsByTagUid(tagUid: String): Boolean
    fun deleteByPetId(petId: UUID)
}