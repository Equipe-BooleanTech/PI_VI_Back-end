package edu.fatec.petwise.domain.entity

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Medication(
    val id: UUID? = null,
    val petId: UUID,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val prescribedBy: UUID,
    val instructions: String? = null,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Nome do medicamento não pode estar vazio" }
        require(dosage.isNotBlank()) { "Dosagem não pode estar vazia" }
        require(frequency.isNotBlank()) { "Frequência não pode estar vazia" }
        require(!endDate.isBefore(startDate)) { "Data final não pode ser anterior à data inicial" }
    }

    fun isExpired(): Boolean = LocalDate.now().isAfter(endDate)

    fun deactivate(): Medication = this.copy(
        active = false,
        updatedAt = LocalDateTime.now()
    )

    fun updateInstructions(instructions: String): Medication = this.copy(
        instructions = instructions,
        updatedAt = LocalDateTime.now()
    )
}
