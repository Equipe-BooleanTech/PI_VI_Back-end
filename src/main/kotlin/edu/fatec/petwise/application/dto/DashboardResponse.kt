package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.ActionType
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.ReminderPriority
import edu.fatec.petwise.domain.enums.ReminderType
import java.time.LocalDateTime

data class StatusCardResponse(
    val totalPets: Int,
    val healthyPets: Int,
    val petsNeedingAttention: Int,
    val upcomingAppointments: Int,
    val pendingVaccines: Int,
    val recentAppointments: Int,
    val lastUpdate: LocalDateTime = LocalDateTime.now()
)

data class PetStatusSummary(
    val petId: String,
    val petName: String,
    val species: String,
    val healthStatus: HealthStatus,
    val lastCheckup: String? = null,
    val nextAppointment: String? = null
)


data class QuickActionResponse(
    val actions: List<QuickActionItem>
)

data class QuickActionItem(
    val id: String,
    val title: String,
    val description: String,
    val actionType: ActionType,
    val icon: String,
    val color: String,
    val enabled: Boolean = true,
    val priority: Int = 0
)



data class ReminderResponse(
    val reminders: List<ReminderItem>,
    val summary: ReminderSummary
)

data class ReminderItem(
    val id: String,
    val title: String,
    val description: String,
    val reminderType: ReminderType,
    val priority: ReminderPriority,
    val dueDate: String?,
    val petId: String?,
    val petName: String?,
    val isCompleted: Boolean = false,
    val relatedEntityId: String? = null
)

data class ReminderSummary(
    val totalReminders: Int,
    val overdueReminders: Int,
    val todayReminders: Int,
    val weekReminders: Int,
    val monthReminders: Int,
    val criticalReminders: Int
)



