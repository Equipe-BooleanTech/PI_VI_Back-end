package edu.fatec.petwise.infrastructure.persistence.adapter

import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.infrastructure.persistence.entity.PetEntity
import edu.fatec.petwise.infrastructure.persistence.jpa.JpaPetRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PetRepositoryAdapter(
    private val jpaPetRepository: JpaPetRepository
) : PetRepository {

    override fun save(pet: Pet): Pet {
        val entity = pet.toEntity()
        val saved = jpaPetRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): Pet? {
        return jpaPetRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Pet> {
        return jpaPetRepository.findAll().map { it.toDomain() }
    }

    override fun findByOwnerId(ownerId: UUID): List<Pet> {
        return jpaPetRepository.findByOwnerId(ownerId).map { it.toDomain() }
    }

    override fun findActiveByOwnerId(ownerId: UUID): List<Pet> {
        return jpaPetRepository.findByOwnerIdAndActive(ownerId, true).map { it.toDomain() }
    }

    override fun findFavoritesByOwnerId(ownerId: UUID): List<Pet> {
        return jpaPetRepository.findByOwnerIdAndIsFavorite(ownerId, true).map { it.toDomain() }
    }

    override fun searchByNameAndOwnerId(query: String, ownerId: UUID): List<Pet> {
        return jpaPetRepository.findByOwnerIdAndNameContainingIgnoreCase(ownerId, query).map { it.toDomain() }
    }

    override fun existsById(id: UUID): Boolean {
        return jpaPetRepository.existsById(id)
    }

    override fun delete(id: UUID) {
        jpaPetRepository.deleteById(id)
    }

    override fun update(pet: Pet): Pet {
        val entity = pet.toEntity()
        val saved = jpaPetRepository.save(entity)
        return saved.toDomain()
    }

    private fun Pet.toEntity() = PetEntity(
        id = this.id,
        name = this.name,
        species = this.species,
        breed = this.breed,
        birthDate = this.birthDate,
        weight = this.weight,
        ownerId = this.ownerId,
        isFavorite = this.isFavorite,
        healthStatus = this.healthStatus,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun PetEntity.toDomain() = Pet(
        id = this.id,
        name = this.name,
        species = this.species,
        breed = this.breed,
        birthDate = this.birthDate,
        weight = this.weight,
        ownerId = this.ownerId,
        isFavorite = this.isFavorite,
        healthStatus = this.healthStatus,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
