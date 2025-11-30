package edu.fatec.petwise.application.dto

import java.time.LocalDateTime
import java.util.UUID


data class PetWithOwnerResponse(
    
    val petId: UUID,
    val petName: String,
    val breed: String,
    val species: String,
    val gender: String,
    val age: Int,
    val weight: Double,
    val healthStatus: String,
    val birthDate: LocalDateTime?,
    val healthHistory: String,
    val profileImageUrl: String?,
    val isFavorite: Boolean,
    val nextAppointment: LocalDateTime?,
    
    
    val ownerId: UUID,
    val ownerName: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val ownerCpf: String?,
    val ownerUserType: String,
    
    
    val nfcTagUid: String,
    val lastCheckIn: LocalDateTime = LocalDateTime.now()
)
