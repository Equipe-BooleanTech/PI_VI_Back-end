package edu.fatec.petwise.domain.enums

enum class AppointmentStatus {
    AGENDADA,    // Consulta agendada
    CONFIRMADA,  // Cliente confirmou presença
    EM_ANDAMENTO,// Consulta em andamento
    CONCLUIDA,   // Consulta finalizada
    CANCELADA,   // Cancelada pelo cliente ou veterinário
    NAO_COMPARECEU // Cliente não compareceu (no-show)
}
