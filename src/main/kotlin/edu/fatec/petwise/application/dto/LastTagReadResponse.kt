package edu.fatec.petwise.application.dto

import java.time.LocalDateTime
import java.util.UUID

/**
 * 🆕 IoT: Cache para última leitura de tag
 * Usado para sincronizar ESP32 com Frontend
 */
data class LastTagReadResponse(
    val tagUid: String,
    val readerId: String,
    val timestamp: LocalDateTime,
    val petFound: Boolean,
    val petData: PetWithOwnerResponse? = null
)
