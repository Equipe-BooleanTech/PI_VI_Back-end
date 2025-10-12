package edu.fatec.petwise.domain.entity

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Vaccine(
    val id: UUID? = null,
    val petId: UUID,
    val name: String,
    val manufacturer: String?,
    val batchNumber: String?,
    val applicationDate: LocalDate,
    val nextDoseDate: LocalDate?,
    val veterinaryId: UUID,
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Nome da vacina não pode estar vazio" }
        require(!applicationDate.isAfter(LocalDate.now())) { "Data de aplicação não pode ser futura" }
        nextDoseDate?.let {
            require(it.isAfter(applicationDate)) { "Data da próxima dose deve ser posterior à data de aplicação" }
        }
    }

    fun isDueForNextDose(): Boolean {
        return nextDoseDate?.let { it.isBefore(LocalDate.now()) || it.isEqual(LocalDate.now()) } ?: false
    }

    fun addNotes(notes: String): Vaccine = this.copy(
        notes = notes,
        updatedAt = LocalDateTime.now()
    )
}
