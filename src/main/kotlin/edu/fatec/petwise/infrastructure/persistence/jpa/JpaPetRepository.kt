package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.PetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaPetRepository : JpaRepository<PetEntity, UUID> {
    fun findByOwnerId(ownerId: UUID): List<PetEntity>
    fun findByOwnerIdAndActive(ownerId: UUID, active: Boolean): List<PetEntity>
    fun findByOwnerIdAndIsFavorite(ownerId: UUID, isFavorite: Boolean): List<PetEntity>
    
    @Query("SELECT p FROM PetEntity p WHERE p.ownerId = :ownerId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findByOwnerIdAndNameContainingIgnoreCase(
        @Param("ownerId") ownerId: UUID,
        @Param("query") query: String
    ): List<PetEntity>
}
