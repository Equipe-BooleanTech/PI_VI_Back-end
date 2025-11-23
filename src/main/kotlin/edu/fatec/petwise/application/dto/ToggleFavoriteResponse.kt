package edu.fatec.petwise.application.dto

import java.util.UUID

data class ToggleFavoriteResponse(
    val petId: UUID,
    val isFavorite: Boolean
)
