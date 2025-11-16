package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.PetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaPetRepository: JpaRepository<PetEntity, UUID> {
    fun findByOwnerId(ownerId: UUID): List<PetEntity>
    fun findActiveByOwnerId(ownerId: UUID): List<PetEntity>
    fun findFavoritesByOwnerId(ownerId: UUID): List<PetEntity>
    fun searchByNameAndOwnerId(query: String, ownerId: UUID): List<PetEntity>
    fun searchByName(query: String): List<PetEntity>
}
