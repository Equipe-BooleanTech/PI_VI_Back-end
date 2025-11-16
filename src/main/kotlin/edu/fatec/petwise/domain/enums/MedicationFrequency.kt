package edu.fatec.petwise.domain.enums

enum class MedicationFrequency(val displayName: String, val dailyCount: Int) {
    ONCE_DAILY("1x ao dia", 1),
    TWICE_DAILY("2x ao dia", 2),
    THREE_TIMES_DAILY("3x ao dia", 3),
    FOUR_TIMES_DAILY("4x ao dia", 4),
    EVERY_8_HOURS("A cada 8 horas", 3),
    EVERY_12_HOURS("A cada 12 horas", 2),
    AS_NEEDED("Conforme necessário", 0),
    OTHER("Outro", 1);

    companion object {
        fun fromDisplayName(displayName: String): MedicationFrequency? =
            values().find { it.displayName == displayName }
    }
}
