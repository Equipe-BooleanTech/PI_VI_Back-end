package edu.fatec.petwise.domain.enums

enum class VaccinationStatus {
    AGENDADA, APLICADA, ATRASADA, CANCELADA;

    fun getDisplayName(): String = when (this) {
        AGENDADA -> "Agendada"
        APLICADA -> "Aplicada"
        ATRASADA -> "Atrasada"
        CANCELADA -> "Cancelada"
    }
}