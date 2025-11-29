package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

class PetTag(
    val id: UUID? = null,
    val tagUid: String,
    val petId: UUID,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)