package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Pet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PetRepository : JpaRepository<Pet, UUID> {
    fun save(pet: Pet): Pet
    override fun findById(id: UUID): Optional<Pet>
    override fun findAll(): List<Pet>
    fun findByOwnerId(ownerId: UUID): List<Pet>
    fun findActiveByOwnerId(ownerId: UUID): List<Pet>
    fun findFavoritesByOwnerId(ownerId: UUID): List<Pet>
    fun searchByNameAndOwnerId(query: String, ownerId: UUID): List<Pet>
    override fun existsById(id: UUID): Boolean
    fun delete(id: UUID)
    fun update(pet: Pet): Pet
    fun findByOwnerIdAndAtivoTrue(ownerId: UUID): List<Pet>
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): Pet?
    fun existsByIdAndOwnerId(id: UUID, ownerId: UUID): Boolean
    fun countByOwnerIdAndAtivoTrue(ownerId: UUID): Long
    fun findByOwnerIdAndEspecieAndAtivoTrue(ownerId: UUID, especie: String): List<Pet>
}
