package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.enums.PetGender
import jakarta.validation.constraints.*

data class CreatePetRequest(
    @field:NotBlank(message = "{pet.name.required}")
    @field:Size(min = 2, max = 50, message = "{pet.name.size}")
    val name: String,
    
    @field:NotBlank(message = "{pet.breed.required}")
    @field:Size(min = 2, max = 50, message = "{pet.breed.size}")
    val breed: String,
    
    @field:NotNull(message = "{pet.species.required}")
    val species: PetSpecies,
    
    @field:NotNull(message = "{pet.gender.required}")
    val gender: PetGender,
    
    @field:Min(value = 0, message = "{pet.age.min}")
    @field:Max(value = 50, message = "{pet.age.max}")
    val age: Int,
    
    @field:Min(value = 1, message = "{pet.weight.min}")
    val weight: Float,
    
    @field:NotNull(message = "{pet.healthStatus.required}")
    val healthStatus: HealthStatus,
    
    val healthHistory: String = "",
    
    val profileImageUrl: String? = null
)

data class UpdatePetRequest(
    val name: String?,
    val breed: String?,
    val weight: Float?,
    val age: Int?,
    val healthHistory: String?,
    val profileImageUrl: String?,
    val nextAppointment: String?
)

data class UpdateHealthStatusRequest(
    @field:NotNull(message = "{pet.healthStatus.required}")
    val healthStatus: HealthStatus
)

data class PetFilterRequest(
    val species: PetSpecies? = null,
    val healthStatus: HealthStatus? = null,
    val favoritesOnly: Boolean = false,
    val searchQuery: String = ""
)

data class PetResponse(
    val id: String,
    val name: String,
    val breed: String,
    val species: String,
    val gender: String,
    val age: Int,
    val weight: Float,
    val healthStatus: String,
    val ownerName: String,
    val ownerPhone: String,
    val healthHistory: String,
    val profileImageUrl: String?,
    val isFavorite: Boolean,
    val nextAppointment: String?,
    val createdAt: String,
    val updatedAt: String
)
