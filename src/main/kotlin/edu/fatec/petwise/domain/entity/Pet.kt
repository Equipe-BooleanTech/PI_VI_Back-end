package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetGender
import edu.fatec.petwise.domain.enums.PetSpecies
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

class Pet(
    var id: UUID? = null,

    val ownerId: UUID,

    var name: String,

    var breed: String,

    var species: PetSpecies,

    var gender: PetGender,

    var age: Int,

    var weight: Double,

    var healthStatus: HealthStatus,

    var ownerName: String,

    var ownerPhone: String,

    var birthDate: LocalDateTime? = null,

    var healthHistory: String = "",

    var profileImageUrl: String? = null,

    var isFavorite: Boolean = false,

    var nextAppointment: LocalDateTime? = null,

    var active: Boolean = true,

    val createdAt: LocalDateTime,

    var updatedAt: LocalDateTime
)

data class PetFilterOptions(
    val species: PetSpecies? = null,
    val healthStatus: HealthStatus? = null,
    val favoritesOnly: Boolean = false,
    val searchQuery: String = ""
)
