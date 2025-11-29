package edu.fatec.petwise.application.dto

import java.time.LocalDateTime
import java.util.UUID

/**
 * 🆕 IOT: Response completo com dados do Pet + Dono
 * Usado pelo frontend para exibir informações após leitura do cartão NFC
 */
data class PetWithOwnerResponse(
    // === DADOS DO PET ===
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
    
    // === DADOS DO DONO ===
    val ownerId: UUID,
    val ownerName: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val ownerCpf: String?,
    val ownerUserType: String,
    
    // === METADADOS ===
    val nfcTagUid: String,
    val lastCheckIn: LocalDateTime = LocalDateTime.now()
)
