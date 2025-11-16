package edu.fatec.petwise.domain.enums

enum class ActionType(val displayName: String) {
    SCHEDULE_APPOINTMENT("Agendar Consulta"),
    ADD_PET("Adicionar Pet"),
    VIEW_VACCINATION_CARD("Ver Carteira de Vacinação"),
    EMERGENCY_CONTACT("Contato de Emergência"),
    HEALTH_RECORD("Histórico de Saúde"),
    MEDICATION_REMINDER("Lembrete de Medicamento"),
    UPDATE_PROFILE("Atualizar Perfil"),
    VIEW_REPORTS("Ver Relatórios")
}
