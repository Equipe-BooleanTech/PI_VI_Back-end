package edu.fatec.petwise.domain.entity

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Pet(
    val id: UUID? = null,
    val name: String,
    val species: String,
    val breed: String?,
    val birthDate: LocalDate,
    val weight: Double?,
    val tutorId: UUID,
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
            require(it > 0) { "Peso deve ser maior que zero" }
        }
    }

    fun calculateAge(): Int {
        return LocalDate.now().year - birthDate.year
    }

    fun deactivate(): Pet = this.copy(active = false, updatedAt = LocalDateTime.now())
    
    fun update(
        name: String? = null,
        breed: String? = null,
        weight: Double? = null
    ): Pet = this.copy(
        name = name ?: this.name,
        breed = breed ?: this.breed,
        weight = weight ?: this.weight,
        updatedAt = LocalDateTime.now()
    )
}
