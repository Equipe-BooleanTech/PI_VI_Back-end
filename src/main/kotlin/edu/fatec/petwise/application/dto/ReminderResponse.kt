package edu.fatec.petwise.application.dto

import java.time.LocalDateTime

enum class ReminderType {
    CONSULTA_PROXIMA,      // Consulta se aproximando
    VACINA_PENDENTE,       // Vacinação pendente
    MEDICAMENTO,           // Horário de medicamento
    ANIVERSARIO_PET,       // Aniversário do pet
    RETORNO_CONSULTA       // Lembrete de retorno
}
enum class ReminderPriority {
    BAIXA,
    MEDIA,
    ALTA,
    URGENTE
}
data class ReminderResponse(
    val id: String,
    val tipo: ReminderType,
    val titulo: String,
    val mensagem: String,
    val dataHora: LocalDateTime,
    val prioridade: ReminderPriority,
    val petId: String? = null,
    val petNome: String? = null,
    val icone: String,
    val cor: String,
    val visualizado: Boolean = false
)
