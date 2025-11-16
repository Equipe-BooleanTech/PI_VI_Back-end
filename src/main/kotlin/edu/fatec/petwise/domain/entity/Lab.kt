package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

class Lab(
    var id: UUID? = null,
    var name: String,
    var contactInfo: String?,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
