package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.QuickActionResponse
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetQuickActionsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String): List<QuickActionResponse> {
        val ownerId = UUID.fromString(userId)
        
        // Verificar se o usuário tem pets cadastrados
        val hasPets = petRepository.countByOwnerIdAndAtivoTrue(ownerId) > 0
        
        logger.info("Ações rápidas geradas para usuário $userId")
        
        return listOf(
            QuickActionResponse(
                id = "adicionar_pet",
                titulo = "Adicionar Pet",
                descricao = "Cadastre um novo pet no sistema",
                icone = "add_circle",
                rota = "/pets/novo",
                cor = "#4CAF50",
                habilitada = true
            ),
            QuickActionResponse(
                id = "agendar_consulta",
                titulo = "Agendar Consulta",
                descricao = "Marque uma consulta veterinária",
                icone = "event_available",
                rota = "/consultas/agendar",
                cor = "#2196F3",
                habilitada = hasPets,
                motivoDesabilitada = if (!hasPets) "Cadastre um pet antes de agendar consultas" else null
            ),
            QuickActionResponse(
                id = "ver_meus_pets",
                titulo = "Meus Pets",
                descricao = "Veja todos os seus pets cadastrados",
                icone = "pets",
                rota = "/pets",
                cor = "#9C27B0",
                habilitada = hasPets,
                motivoDesabilitada = if (!hasPets) "Você ainda não possui pets cadastrados" else null
            ),
            QuickActionResponse(
                id = "historico_consultas",
                titulo = "Histórico de Consultas",
                descricao = "Consulte o histórico completo",
                icone = "history",
                rota = "/consultas/historico",
                cor = "#FF9800",
                habilitada = true
            ),
            QuickActionResponse(
                id = "veterinarios",
                titulo = "Buscar Veterinários",
                descricao = "Encontre veterinários disponíveis",
                icone = "local_hospital",
                rota = "/veterinarios",
                cor = "#F44336",
                habilitada = true
            ),
            QuickActionResponse(
                id = "perfil",
                titulo = "Meu Perfil",
                descricao = "Atualize suas informações pessoais",
                icone = "account_circle",
                rota = "/perfil",
                cor = "#607D8B",
                habilitada = true
            )
        )
    }
}
