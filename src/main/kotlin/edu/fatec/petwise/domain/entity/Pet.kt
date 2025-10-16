package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.enums.PetGender
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Pet(
    val id: UUID? = null,
    val name: String,
    val breed: String,
    val species: PetSpecies,
    val gender: PetGender,
    val age: Int,
    val weight: Float,
    val healthStatus: HealthStatus = HealthStatus.GOOD,
    val ownerId: UUID,
    val healthHistory: String = "",
    val profileImageUrl: String? = null,
    val isFavorite: Boolean = false,
    val nextAppointment: String? = null,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Nome do pet não pode estar vazio" }
        require(breed.isNotBlank()) { "Raça do pet não pode estar vazia" }
        require(age >= 0) { "Idade não pode ser negativa" }
        require(weight > 0) { "Peso deve ser maior que zero" }
    }

    fun deactivate(): Pet = this.copy(active = false, updatedAt = LocalDateTime.now())
    
    fun toggleFavorite(): Pet = this.copy(isFavorite = !isFavorite, updatedAt = LocalDateTime.now())
    
    fun updateHealthStatus(newStatus: HealthStatus): Pet = this.copy(
        healthStatus = newStatus,
        updatedAt = LocalDateTime.now()
    )
    
    fun update(
        name: String? = null,
        breed: String? = null,
        weight: Float? = null,
        age: Int? = null,
        healthHistory: String? = null,
        profileImageUrl: String? = null,
        nextAppointment: String? = null
    ): Pet = this.copy(
        name = name ?: this.name,
        breed = breed ?: this.breed,
        weight = weight ?: this.weight,
        age = age ?: this.age,
        healthHistory = healthHistory ?: this.healthHistory,
        profileImageUrl = profileImageUrl ?: this.profileImageUrl,
        nextAppointment = nextAppointment ?: this.nextAppointment,
        updatedAt = LocalDateTime.now()
    )
    
    companion object {
        fun create(
            name: String,
            breed: String,
            species: PetSpecies,
            gender: PetGender,
            age: Int,
            weight: Float,
            ownerId: UUID,
            healthStatus: HealthStatus = HealthStatus.GOOD,
            healthHistory: String = "",
            profileImageUrl: String? = null
        ): Pet {
            return Pet(
                name = name,
                breed = breed,
                species = species,
                gender = gender,
                age = age,
                weight = weight,
                ownerId = ownerId,
                healthStatus = healthStatus,
                healthHistory = healthHistory,
                profileImageUrl = profileImageUrl
            )
        }
    }
}
