package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
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
    
    @field:Positive(message = "{pet.weight.positive}")
    val weight: Double?,
    
    @field:NotBlank(message = "{pet.tutorId.required}")
    val tutorId: String
)

data class UpdatePetRequest(
    @field:Size(min = 2, max = 50, message = "{pet.name.size}")
    val name: String?,
    
    @field:Size(max = 50, message = "{pet.breed.size}")
    val breed: String?,
    
    @field:Positive(message = "{pet.weight.positive}")
    val weight: Double?
)

data class PetResponse(
    val id: String,
    val name: String,
    val species: String,
    val breed: String?,
    val birthDate: String,
    val age: Int,
    val weight: Double?,
    val tutorId: String,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
