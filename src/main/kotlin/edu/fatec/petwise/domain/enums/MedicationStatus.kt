package edu.fatec.petwise.domain.enums

enum class MedicationStatus(val displayName: String, val color: String) {
    ACTIVE("Ativo", "#4CAF50"),
    COMPLETED("Concluído", "#2196F3"),
    PAUSED("Pausado", "#FF9800"),
    CANCELLED("Cancelado", "#F44336");

    companion object {
        fun fromDisplayName(displayName: String): MedicationStatus? =
            values().find { it.displayName == displayName }
    }
}
