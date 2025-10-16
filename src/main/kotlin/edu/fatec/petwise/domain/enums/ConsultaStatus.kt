package edu.fatec.petwise.domain.enums

enum class ConsultaStatus(val displayName: String, val color: String) {
    SCHEDULED("Agendada", "#2196F3"),
    IN_PROGRESS("Em Andamento", "#FF9800"),
    COMPLETED("Concluída", "#4CAF50"),
    CANCELLED("Cancelada", "#F44336"),
    RESCHEDULED("Remarcada", "#9C27B0")
}