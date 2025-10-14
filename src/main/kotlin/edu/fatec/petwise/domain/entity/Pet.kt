package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.HealthStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Pet(
    val id: UUID? = null,
    val name: String,
    val species: String,
    val breed: String?,
    val birthDate: LocalDate,
    val weight: BigDecimal?,
    val ownerId: UUID,
    val isFavorite: Boolean = false,
    val healthStatus: HealthStatus = HealthStatus.SAUDAVEL,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Nome do pet não pode estar vazio" }
        require(species.isNotBlank()) { "Espécie do pet não pode estar vazia" }
        require(birthDate.isBefore(LocalDate.now()) || birthDate.isEqual(LocalDate.now())) {
            "Data de nascimento não pode ser futura"
        }
        weight?.let {
            require(it > BigDecimal.ZERO) { "Peso deve ser maior que zero" }
        }
    }

    fun calculateAge(): Int {
        return LocalDate.now().year - birthDate.year
    }

    fun deactivate(): Pet = this.copy(active = false, updatedAt = LocalDateTime.now())
    
    fun toggleFavorite(): Pet = this.copy(isFavorite = !isFavorite, updatedAt = LocalDateTime.now())
    
    fun updateHealthStatus(newStatus: HealthStatus): Pet = this.copy(
        healthStatus = newStatus,
        updatedAt = LocalDateTime.now()
    )
    
    fun update(
        name: String? = null,
        breed: String? = null,
        weight: BigDecimal? = null
    ): Pet = this.copy(
        name = name ?: this.name,
        breed = breed ?: this.breed,
        weight = weight ?: this.weight,
        updatedAt = LocalDateTime.now()
    )
    
    companion object {
        fun create(
            name: String,
            species: String,
            breed: String?,
            birthDate: LocalDate,
            weight: BigDecimal?,
            ownerId: UUID,
            healthStatus: HealthStatus = HealthStatus.SAUDAVEL
        ): Pet {
            return Pet(
                name = name,
                species = species,
                breed = breed,
                birthDate = birthDate,
                weight = weight,
                ownerId = ownerId,
                healthStatus = healthStatus
            )
        }
    }
}
