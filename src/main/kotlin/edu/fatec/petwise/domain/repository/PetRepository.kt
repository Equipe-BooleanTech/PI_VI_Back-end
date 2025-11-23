package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.entity.PetFilterOptions
import java.util.Optional
import java.util.UUID

interface PetRepository {
    fun findAll(): List<Pet>
    fun findByOwnerId(ownerId: UUID): List<Pet>
    fun findActiveByOwnerId(ownerId: UUID): List<Pet>
    fun findFavoritesByOwnerId(ownerId: UUID): List<Pet>
    fun searchByNameAndOwnerId(query: String, ownerId: UUID): List<Pet>
    fun searchByName(query: String): List<Pet>
    fun filterPets(options: PetFilterOptions): List<Pet>
    fun findById(id: UUID): Optional<Pet>
    fun save(pet: Pet): Pet
    fun deleteById(id: UUID)
}
