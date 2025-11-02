package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.*
import edu.fatec.petwise.application.usecase.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Endpoints do dashboard do cliente")
@SecurityRequirement(name = "bearer-key")
class DashboardController(
    @Autowired private val getStatusCardsUseCase: GetStatusCardsUseCase,
    @Autowired private val getQuickActionsUseCase: GetQuickActionsUseCase,
    @Autowired private val getRemindersUseCase: GetRemindersUseCase
) {

    @GetMapping("/status-cards")
    @Operation(
        summary = "Obter cards de status do dashboard",
        description = "Retorna estatísticas e status dos pets do usuário"
    )
    fun getStatusCards(authentication: Authentication): ResponseEntity<StatusCardResponse> {
        val userId = authentication.name
        val response = getStatusCardsUseCase.execute(userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/quick-actions")
    @Operation(
        summary = "Obter ações rápidas do dashboard",
        description = "Retorna lista de ações disponíveis para o usuário"
    )
    fun getQuickActions(authentication: Authentication): ResponseEntity<QuickActionResponse> {
        val userId = authentication.name
        val response = getQuickActionsUseCase.execute(userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/reminders")
    @Operation(
        summary = "Obter lembretes do dashboard",
        description = "Retorna lista de lembretes e avisos para o usuário"
    )
    fun getReminders(authentication: Authentication): ResponseEntity<ReminderResponse> {
        val userId = authentication.name
        val response = getRemindersUseCase.execute(userId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/reminders/{reminderId}/complete")
    @Operation(
        summary = "Marcar lembrete como concluído",
        description = "Marca um lembrete específico como concluído"
    )
    fun completeReminder(
        @PathVariable reminderId: String,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        // Implementar lógica para marcar lembrete como concluído
        val result = mapOf("message" to "Lembrete marcado como concluído com sucesso")
        return ResponseEntity.ok(result)
    }

    @PostMapping("/reminders/{reminderId}/dismiss")
    @Operation(
        summary = "Dispensar lembrete",
        description = "Remove um lembrete da lista (não completa)"
    )
    fun dismissReminder(
        @PathVariable reminderId: String,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        // Implementar lógica para dispensar lembrete
        val result = mapOf("message" to "Lembrete dispensado com sucesso")
        return ResponseEntity.ok(result)
    }
}