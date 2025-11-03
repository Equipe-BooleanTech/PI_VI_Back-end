package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.StatusCardResponse
import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
@Service
class GetStatusCardsUseCase(
    private val petRepository: PetRepository,
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun execute(userId: String): List<StatusCardResponse> {
        val ownerId = UUID.fromString(userId)
        val now = LocalDateTime.now()
        
        // Card 1: Total de Pets
        val totalPets = petRepository.countByOwnerIdAndAtivoTrue(ownerId).toInt()
        
        // Card 2: Consultas Próximas
        val consultasProximas = appointmentRepository.findUpcomingAppointments(ownerId, now).size
        
        // Card 3: Consultas Realizadas
        val consultasRealizadas = appointmentRepository.countByOwnerIdAndStatus(
            ownerId, 
            AppointmentStatus.CONCLUIDA
        ).toInt()
        
        // Card 4: Consultas Pendentes (agendadas + confirmadas)
        val consultasPendentes = appointmentRepository.countByOwnerIdAndStatus(
            ownerId, 
            AppointmentStatus.AGENDADA
        ).toInt() + appointmentRepository.countByOwnerIdAndStatus(
            ownerId, 
            AppointmentStatus.CONFIRMADA
        ).toInt()
        
        logger.info("Status cards gerados para usuário $userId")
        
        return listOf(
            StatusCardResponse(
                tipo = "pets",
                titulo = "Meus Pets",
                valor = totalPets,
                icone = "pets",
                cor = "#4CAF50",
                descricao = if (totalPets == 1) "1 pet cadastrado" else "$totalPets pets cadastrados"
            ),
            StatusCardResponse(
                tipo = "consultas_proximas",
                titulo = "Próximas Consultas",
                valor = consultasProximas,
                icone = "event",
                cor = "#2196F3",
                descricao = if (consultasProximas == 1) "1 consulta agendada" else "$consultasProximas consultas agendadas"
            ),
            StatusCardResponse(
                tipo = "consultas_realizadas",
                titulo = "Consultas Realizadas",
                valor = consultasRealizadas,
                icone = "check_circle",
                cor = "#9C27B0",
                descricao = "Total de consultas concluídas"
            ),
            StatusCardResponse(
                tipo = "consultas_pendentes",
                titulo = "Consultas Pendentes",
                valor = consultasPendentes,
                icone = "schedule",
                cor = "#FF9800",
                descricao = "Aguardando confirmação ou realização"
            )
        )
    }
}
