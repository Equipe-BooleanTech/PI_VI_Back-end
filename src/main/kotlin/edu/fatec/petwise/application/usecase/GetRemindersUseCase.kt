package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ReminderResponse
import edu.fatec.petwise.application.dto.ReminderType
import edu.fatec.petwise.application.dto.ReminderPriority
import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class GetRemindersUseCase(
    private val appointmentRepository: AppointmentRepository,
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String): List<ReminderResponse> {
        val ownerId = UUID.fromString(userId)
        val now = LocalDateTime.now()
        val today = LocalDate.now()
        val reminders = mutableListOf<ReminderResponse>()
        
        // Lembretes de consultas próximas
        val upcomingAppointments = appointmentRepository.findUpcomingAppointments(ownerId, now)
            .take(5) // Limitar aos próximos 5
        
        upcomingAppointments.forEach { appointment ->
            val pet = petRepository.findById(appointment.petId).orElse(null)
            val hoursUntil = ChronoUnit.HOURS.between(now, appointment.appointmentDatetime)
            
            val (prioridade, cor) = when {
                hoursUntil < 2 -> ReminderPriority.URGENTE to "#F44336"
                hoursUntil < 24 -> ReminderPriority.ALTA to "#FF9800"
                hoursUntil < 72 -> ReminderPriority.MEDIA to "#FFC107"
                else -> ReminderPriority.BAIXA to "#4CAF50"
            }
            
            val mensagem = when {
                hoursUntil < 1 -> "A consulta começa em menos de 1 hora!"
                hoursUntil < 24 -> "A consulta é hoje às ${appointment.appointmentDatetime.toLocalTime()}"
                else -> {
                    val daysUntil = ChronoUnit.DAYS.between(today, appointment.appointmentDatetime.toLocalDate())
                    "Consulta em $daysUntil ${if (daysUntil == 1L) "dia" else "dias"}"
                }
            }
            
            reminders.add(
                ReminderResponse(
                    id = UUID.randomUUID(),
                    tipo = ReminderType.CONSULTA_PROXIMA,
                    titulo = "Consulta: ${appointment.motivo}",
                    mensagem = mensagem,
                    dataHora = appointment.appointmentDatetime,
                    prioridade = prioridade,
                    petId = pet?.id,
                    petNome = pet?.nome,
                    icone = "event",
                    cor = cor
                )
            )
        }
        
        // Lembretes de aniversário dos pets (próximos 30 dias)
        val pets = petRepository.findByOwnerIdAndAtivoTrue(ownerId)
        
        pets.forEach { pet ->
            pet.dataNascimento?.let { dataNascimento ->
                // Calcular próximo aniversário
                val proximoAniversario = dataNascimento.withYear(today.year)
                val aniversarioAjustado = if (proximoAniversario.isBefore(today)) {
                    proximoAniversario.plusYears(1)
                } else {
                    proximoAniversario
                }
                
                val daysUntil = ChronoUnit.DAYS.between(today, aniversarioAjustado)
                
                if (daysUntil in 0..30) {
                    val idade = today.year - dataNascimento.year + 1
                    val mensagem = when (daysUntil) {
                        0L -> "🎉 É aniversário do ${pet.nome} hoje! Ele faz $idade ${if (idade == 1) "ano" else "anos"}!"
                        1L -> "O aniversário do ${pet.nome} é amanhã! Ele fará $idade ${if (idade == 1) "ano" else "anos"}"
                        else -> "Faltam $daysUntil dias para o aniversário do ${pet.nome} ($idade ${if (idade == 1) "ano" else "anos"})"
                    }
                    
                    val prioridade = when (daysUntil) {
                        0L -> ReminderPriority.URGENTE
                        in 1..3 -> ReminderPriority.ALTA
                        in 4..7 -> ReminderPriority.MEDIA
                        else -> ReminderPriority.BAIXA
                    }
                    
                    reminders.add(
                        ReminderResponse(
                            id = UUID.randomUUID(),
                            tipo = ReminderType.ANIVERSARIO_PET,
                            titulo = "Aniversário: ${pet.nome}",
                            mensagem = mensagem,
                            dataHora = aniversarioAjustado.atStartOfDay(),
                            prioridade = prioridade,
                            petId = pet.id,
                            petNome = pet.nome,
                            icone = "cake",
                            cor = "#E91E63"
                        )
                    )
                }
            }
        }
        
        logger.info("${reminders.size} lembretes gerados para usuário $userId")
        
        // Ordenar por prioridade (descendente) e depois por data (ascendente)
        return reminders.sortedWith(
            compareByDescending<ReminderResponse> { it.prioridade.ordinal }
                .thenBy { it.dataHora }
        )
    }
}
