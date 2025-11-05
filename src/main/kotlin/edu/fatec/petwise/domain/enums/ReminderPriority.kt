package edu.fatec.petwise.domain.enums
enum class ReminderPriority(val level: Int, val color: String, val displayName: String) {
    LOW(1, "#4CAF50", "Baixa"),
    NORMAL(2, "#2196F3", "Normal"),
    HIGH(3, "#FF9800", "Alta"),
    CRITICAL(4, "#F44336", "Crítica")
}