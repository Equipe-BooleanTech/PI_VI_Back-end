package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.HealthStatus
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate

data class CreatePetRequest(
    @field:NotBlank(message = "{pet.name.required}")
    @field:Size(min = 2, max = 50, message = "{pet.name.size}")
    val name: String,
    
    @field:NotBlank(message = "{pet.species.required}")
    @field:Size(min = 2, max = 30, message = "{pet.species.size}")
    val species: String,
    
    @field:Size(max = 50, message = "{pet.breed.size}")
    val breed: String?,
    
    @field:NotNull(message = "{pet.birthDate.required}")
    @field:PastOrPresent(message = "{pet.birthDate.pastOrPresent}")
    val birthDate: LocalDate,
    
    @field:DecimalMin(value = "0.1", message = "{pet.weight.min}")
    @field:DecimalMax(value = "500.0", message = "{pet.weight.max}")
    val weight: BigDecimal?,
    
    @field:NotNull(message = "{pet.healthStatus.required}")
    val healthStatus: HealthStatus
)

data class UpdatePetRequest(
    @field:NotBlank(message = "{pet.name.notBlank}")
    @field:Size(min = 1, max = 50, message = "{pet.name.size}")
    val name: String,

    @field:NotBlank(message = "{pet.breed.notBlank}")
    @field:Size(min = 1, max = 50, message = "{pet.breed.size}")
    val breed: String,

    @field:DecimalMin(value = "0.1", message = "{pet.weight.min}")
    @field:DecimalMax(value = "500.0", message = "{pet.weight.max}")
    val weight: BigDecimal
)

data class UpdateHealthStatusRequest(
    @field:NotNull(message = "{pet.healthStatus.required}")
    val healthStatus: HealthStatus
)

data class PetResponse(
    val id: String,
    val name: String,
    val species: String,
    val breed: String?,
    val birthDate: String,
    val age: Int,
    val weight: BigDecimal?,
    val ownerId: String,
    val isFavorite: Boolean,
    val healthStatus: String,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
