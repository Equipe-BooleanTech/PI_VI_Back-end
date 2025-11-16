package edu.fatec.petwise.domain.enums

enum class ReminderType(val displayName: String) {
    VACCINATION("Vacinação"),
    APPOINTMENT("Consulta"),
    MEDICATION("Medicação"),
    HEALTH_CHECK("Checkup de Saúde"),
    BIRTHDAY("Aniversário do Pet"),
    DEWORMING("Vermifugação"),
        GROOMING("Banho/Tosa"),
        EMERGENCY("Emergência")
}
