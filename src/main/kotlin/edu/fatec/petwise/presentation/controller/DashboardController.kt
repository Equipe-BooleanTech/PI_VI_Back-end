package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.QuickActionResponse
import edu.fatec.petwise.application.dto.ReminderResponse
import edu.fatec.petwise.application.dto.StatusCardResponse
import edu.fatec.petwise.application.usecase.GetQuickActionsUseCase
import edu.fatec.petwise.application.usecase.GetRemindersUseCase
import edu.fatec.petwise.application.usecase.GetStatusCardsUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val getStatusCardsUseCase: GetStatusCardsUseCase,
    private val getQuickActionsUseCase: GetQuickActionsUseCase,
    private val getRemindersUseCase: GetRemindersUseCase
) {
    

    @GetMapping("/status-cards")
    fun getStatusCards(authentication: Authentication): ResponseEntity<List<StatusCardResponse>> {
        val userId = authentication.name
        val cards = getStatusCardsUseCase.execute(userId)
        return ResponseEntity.ok(cards)
    }

    @GetMapping("/quick-actions")
    fun getQuickActions(authentication: Authentication): ResponseEntity<List<QuickActionResponse>> {
        val userId = authentication.name
        val actions = getQuickActionsUseCase.execute(userId)
        return ResponseEntity.ok(actions)
    }

    @GetMapping("/reminders")
    fun getReminders(authentication: Authentication): ResponseEntity<List<ReminderResponse>> {
        val userId = authentication.name
        val reminders = getRemindersUseCase.execute(userId)
        return ResponseEntity.ok(reminders)
    }
}
