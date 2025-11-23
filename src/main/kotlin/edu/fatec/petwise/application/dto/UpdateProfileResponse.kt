package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.User
import java.util.UUID

data class UpdateProfileResponse(
    val user: UserResponse,
    val requiresLogout: Boolean
) {
    companion object {
        fun fromEntity(user: User, requiresLogout: Boolean = false): UpdateProfileResponse =
            UpdateProfileResponse(
                user = UserResponse.fromEntity(user),
                requiresLogout = requiresLogout
            )
    }
}