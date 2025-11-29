package edu.fatec.petwise.application.dto

import java.time.LocalDateTime
import java.util.UUID

data class IotCheckInRequest(
    val tag_uid: String,
    val reader_id: String
)

data class StartPairingRequest(
    val petId: UUID,
    val readerId: String
)

data class IotCheckInResponse(
    val petId: UUID,
    val petName: String,
    val ownerName: String?,
    val species: String?,
    val ownerPhone: String?,
    val message: String
)

data class RfidTagReadRequest(
    val tagUid: String,
    val readerId: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class RfidTagRegistrationResponse(
    val success: Boolean,
    val tagId: UUID?,
    val tagUid: String,
    val petId: UUID?,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class PairingStatusResponse(
    val isPairing: Boolean,
    val petId: UUID?,
    val message: String
)