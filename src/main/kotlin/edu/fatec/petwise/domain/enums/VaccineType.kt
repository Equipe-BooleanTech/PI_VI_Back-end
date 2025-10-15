package edu.fatec.petwise.domain.enums

enum class VaccineType {
    V8, V10, ANTIRABICA, GRIPE_CANINA, GIARDIA, LEPTOSPIROSE,
    TRIPLE_FELINA, QUADRUPLA_FELINA, LEUCEMIA_FELINA, RAIVA_FELINA,
    OUTRAS;

    fun getDisplayName(): String = when (this) {
        V8 -> "Vacina V8"
        V10 -> "Vacina V10"
        ANTIRABICA -> "Antirrábica"
        GRIPE_CANINA -> "Gripe Canina"
        GIARDIA -> "Giárdia"
        LEPTOSPIROSE -> "Leptospirose"
        TRIPLE_FELINA -> "Tríplice Felina"
        QUADRUPLA_FELINA -> "Quádrupla Felina"
        LEUCEMIA_FELINA -> "Leucemia Felina"
        RAIVA_FELINA -> "Raiva Felina"
        OUTRAS -> "Outras"
    }
}