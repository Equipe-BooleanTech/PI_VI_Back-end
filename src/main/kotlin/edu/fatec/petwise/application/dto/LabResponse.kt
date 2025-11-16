package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Lab
import java.time.LocalDateTime
import java.util.UUID

data class LabResponse(
    val id: UUID?,
    val name: String,
    val contactInfo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(lab: Lab): LabResponse {
            return LabResponse(
                id = lab.id,
                name = lab.name,
                contactInfo = lab.contactInfo,
                createdAt = lab.createdAt,
                updatedAt = lab.updatedAt
            )
        }
    }
}
