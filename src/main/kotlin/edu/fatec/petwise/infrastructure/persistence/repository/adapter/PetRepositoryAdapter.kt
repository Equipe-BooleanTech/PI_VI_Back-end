package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.entity.PetFilterOptions
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaPetRepository
import edu.fatec.petwise.infrastructure.persistence.entity.PetEntity
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class PetRepositoryAdapter(
    private val repository: JpaPetRepository,
    private val userRepository: UserRepository
) : PetRepository {

    override fun findAll(): List<Pet> = repository.findAll().map { it.toDomain() }
    override fun findById(id: UUID): Optional<Pet> = repository.findById(id).map { it.toDomain() }
    override fun findByOwnerId(ownerId: UUID): List<Pet> = repository.findByOwnerId(ownerId).map { it.toDomain() }
    override fun findActiveByOwnerId(ownerId: UUID): List<Pet> = repository.findActiveByOwnerId(ownerId).map { it.toDomain() }
    override fun findFavoritesByOwnerId(ownerId: UUID): List<Pet> = repository.findFavoritesByOwnerId(ownerId).map { it.toDomain() }
    override fun searchByNameAndOwnerId(query: String, ownerId: UUID): List<Pet> = repository.searchByNameAndOwnerId(query, ownerId).map { it.toDomain() }
    override fun searchByName(query: String): List<Pet> = repository.searchByName(query).map { it.toDomain() }
    override fun filterPets(options: PetFilterOptions): List<Pet> {
        
        var pets = repository.findAll().map { it.toDomain() }
        options.species?.let { species -> pets = pets.filter { it.species == species } }
        options.healthStatus?.let { health -> pets = pets.filter { it.healthStatus == health } }
        if (options.favoritesOnly) pets = pets.filter { it.isFavorite }
        if (options.searchQuery.isNotBlank()) pets = pets.filter { it.name.contains(options.searchQuery, ignoreCase = true) }
        return pets
    }
    override fun save(pet: Pet): Pet {
        val entity = pet.toEntity()
        return repository.save(entity).toDomain()
    }
    override fun deleteById(id: UUID) = repository.deleteById(id)

    private fun PetEntity.toDomain(): Pet {
        val user = userRepository.findById(this.ownerId).orElseThrow { IllegalArgumentException("Owner not found") }
        return Pet(
            id = this.id ?: UUID.randomUUID(),
            ownerId = this.ownerId,
            name = this.name,
            breed = this.breed,
            species = this.species,
            gender = this.gender,
            age = this.age,
            weight = this.weight,
            healthStatus = this.healthStatus,
            ownerName = user.fullName,
            ownerPhone = user.phone.value,
            birthDate = this.birthDate,
            healthHistory = this.healthHistory,
            profileImageUrl = this.profileImageUrl,
            isFavorite = this.isFavorite,
            nextAppointment = this.nextAppointment,
            active = this.active,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun Pet.toEntity(): PetEntity = PetEntity(
        name = this.name,
        breed = this.breed,
        species = this.species,
        gender = this.gender,
        age = this.age,
        weight = this.weight,
        healthStatus = this.healthStatus,
        ownerId = this.ownerId,
        birthDate = this.birthDate,
        healthHistory = this.healthHistory,
        profileImageUrl = this.profileImageUrl,
        isFavorite = this.isFavorite,
        nextAppointment = this.nextAppointment,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    ).apply { id = this@toEntity.id }
}
