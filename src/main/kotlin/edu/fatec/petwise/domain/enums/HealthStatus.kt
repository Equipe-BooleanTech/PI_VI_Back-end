package edu.fatec.petwise.domain.enums

enum class HealthStatus(val displayName: String, val color: String) {
    EXCELLENT("Excelente", "#00b942"),
    GOOD("Bom", "#4CAF50"),
    REGULAR("Regular", "#FFC107"),
    ATTENTION("Atenção", "#FF9800"),
    CRITICAL("Crítico", "#F44336")
}