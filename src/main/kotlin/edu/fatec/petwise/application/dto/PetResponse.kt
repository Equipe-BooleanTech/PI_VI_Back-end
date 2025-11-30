package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Pet
import java.time.LocalDateTime
import java.util.UUID

data class PetResponse(
    val id: UUID?,
    val ownerId: UUID,
    val name: String,
    val breed: String,
    val species: String,
    val gender: String,
    val age: Int,
    val weight: Double,
    val healthStatus: String,
    val ownerName: String,
    val ownerPhone: String,
    val birthDate: LocalDateTime?,
    val healthHistory: String,
    val profileImageUrl: String?,
    val isFavorite: Boolean,
    val nextAppointment: LocalDateTime?,
    val active: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,

    
    val hasNfcTag: Boolean = false,
    val nfcPairingStatus: String? = null  
) {
    companion object {
        fun fromEntity(pet: Pet): PetResponse {
            return PetResponse(
                id = pet.id,
                ownerId = pet.ownerId,
                name = pet.name,
                breed = pet.breed,
                species = pet.species.name,
                gender = pet.gender.name,
                age = pet.age,
                weight = pet.weight,
                healthStatus = pet.healthStatus.name,
                ownerName = pet.ownerName,
                ownerPhone = pet.ownerPhone,
                birthDate = pet.birthDate,
                healthHistory = pet.healthHistory,
                profileImageUrl = pet.profileImageUrl,
                isFavorite = pet.isFavorite,
                nextAppointment = pet.nextAppointment,
                active = true, 
                createdAt = pet.createdAt,
                updatedAt = pet.updatedAt
            )
        }
    }
}
