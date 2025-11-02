package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.*
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.enums.ActionType
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.ReminderPriority
import edu.fatec.petwise.domain.enums.ReminderType
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.VaccineRepository


import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class GetStatusCardsUseCase(
    private val petRepository: PetRepository,
    private val appointmentRepository: AppointmentRepository,
    private val vaccineRepository: VaccineRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: String): StatusCardResponse {
        logger.info("Buscando status cards para owner: $ownerId")

        val pets = petRepository.findByOwnerId(UUID.fromString(ownerId))
        val today = LocalDate.now()
        val weekAhead = today.plusDays(7)

        val healthyPets = pets.count { it.healthStatus == HealthStatus.EXCELLENT || it.healthStatus == HealthStatus.GOOD }
        val petsNeedingAttention = pets.count {
            it.healthStatus in listOf(HealthStatus.REGULAR, HealthStatus.ATTENTION, HealthStatus.CRITICAL)
        }

        val upcomingAppointments = appointmentRepository.countUpcomingAppointments(
            ownerId = UUID.fromString(ownerId),
            fromDate = today.atStartOfDay(),
            toDate = weekAhead.atStartOfDay()
        )

        val pendingVaccines = vaccineRepository.countPendingVaccines(UUID.fromString(ownerId))

        val recentAppointments = appointmentRepository.countRecentAppointments(
            ownerId = UUID.fromString(ownerId),
            fromDate = today.minusDays(30).atStartOfDay()
        )

        return StatusCardResponse(
            totalPets = pets.size,
            healthyPets = healthyPets,
            petsNeedingAttention = petsNeedingAttention,
            upcomingAppointments = upcomingAppointments,
            pendingVaccines = pendingVaccines,
            recentAppointments = recentAppointments
        )
    }
}

@Service
class GetQuickActionsUseCase(
    private val petRepository: PetRepository,
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: String): QuickActionResponse {
        logger.info("Buscando quick actions para owner: $ownerId")

        val pets = petRepository.findByOwnerId(UUID.fromString(ownerId))
        val ownerUUID = UUID.fromString(ownerId)

        val actions = mutableListOf<QuickActionItem>()

        // Ação sempre disponível: Adicionar Pet
        actions.add(
            QuickActionItem(
                id = "add-pet",
                title = "Adicionar Pet",
                description = "Cadastre um novo pet na sua conta",
                actionType = ActionType.ADD_PET,
                icon = "pet-plus",
                color = "#4CAF50",
                priority = 10
            )
        )

        // Agendar Consulta (disponível se tem pets)
        if (pets.isNotEmpty()) {
            actions.add(
                QuickActionItem(
                    id = "schedule-appointment",
                    title = "Agendar Consulta",
                    description = "Agende uma consulta veterinária",
                    actionType = ActionType.SCHEDULE_APPOINTMENT,
                    icon = "calendar-plus",
                    color = "#2196F3",
                    priority = 9
                )
            )

            actions.add(
                QuickActionItem(
                    id = "vaccination-card",
                    title = "Carteira de Vacinação",
                    description = "Veja as vacinas dos seus pets",
                    actionType = ActionType.VIEW_VACCINATION_CARD,
                    icon = "vaccination-card",
                    color = "#FF9800",
                    priority = 8
                )
            )

            // Verificar se há lembretes pendentes para destacar
            val hasPendingReminders = hasPendingReminders(ownerUUID)
            if (hasPendingReminders) {
                actions.add(
                    QuickActionItem(
                        id = "medication-reminder",
                        title = "Lembretes de Medicamento",
                        description = "Configure lembretes para medicamentos",
                        actionType = ActionType.MEDICATION_REMINDER,
                        icon = "medication-reminder",
                        color = "#F44336",
                        priority = 7,
                        enabled = true
                    )
                )
            }
        }

        // Ações sempre disponíveis
        actions.addAll(listOf(
            QuickActionItem(
                id = "health-record",
                title = "Histórico de Saúde",
                description = "Veja o histórico médico dos seus pets",
                actionType = ActionType.HEALTH_RECORD,
                icon = "health-record",
                color = "#9C27B0",
                priority = 6
            ),
            QuickActionItem(
                id = "emergency-contact",
                title = "Contato de Emergência",
                description = "Acesso rápido a contatos de emergência",
                actionType = ActionType.EMERGENCY_CONTACT,
                icon = "emergency-contact",
                color = "#F44336",
                priority = 5
            ),
            QuickActionItem(
                id = "update-profile",
                title = "Atualizar Perfil",
                description = "Edite suas informações pessoais",
                actionType = ActionType.UPDATE_PROFILE,
                icon = "edit-profile",
                color = "#607D8B",
                priority = 4
            )
        ))

        return QuickActionResponse(actions.sortedByDescending { it.priority })
    }

    private fun hasPendingReminders(ownerId: UUID): Boolean {
        // Implementar lógica para verificar se há lembretes pendentes
        // Por enquanto, retorna false - implementar baseada nos repositórios
        return false
    }
}

@Service
class GetRemindersUseCase(
    private val petRepository: PetRepository,
    private val appointmentRepository: AppointmentRepository,
    private val vaccineRepository: VaccineRepository,
    private val medicationRepository: MedicationRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: String): ReminderResponse {
        logger.info("Buscando reminders para owner: $ownerId")

        val ownerUUID = UUID.fromString(ownerId)
        val pets = petRepository.findByOwnerId(ownerUUID)
        val now = LocalDateTime.now()
        val today = LocalDate.now()

        val reminders = mutableListOf<ReminderItem>()

        // Lembretes de Vacinação
        pets.forEach { pet ->
            val pendingVaccines = vaccineRepository.findPendingVaccinesByPet(pet.id!!)
            pendingVaccines.forEach { vaccine ->
                val dueDate = vaccine.nextDoseDate
                if (dueDate != null) {
                    val priority = calculateVaccinePriority(dueDate, today)
                    reminders.add(
                        ReminderItem(
                            id = "vaccine-${vaccine.id}",
                            title = "Vacina Pendente",
                            description = "${vaccine.vaccineType.displayName} para ${pet.name}",
                            reminderType = ReminderType.VACCINATION,
                            priority = priority,
                            dueDate = dueDate,
                            petId = pet.id.toString(),
                            petName = pet.name,
                            relatedEntityId = vaccine.id.toString()
                        )
                    )
                }
            }
        }

        // Lembretes de Consulta (próximas 2 semanas)
        val upcomingAppointments = appointmentRepository.findUpcomingAppointments(
            ownerId = ownerUUID,
            fromDate = now,
            toDate = now.plusDays(14)
        )

        upcomingAppointments.forEach { appointment ->
            val pet = pets.find { it.id == appointment.petId }
            reminders.add(
                ReminderItem(
                    id = "appointment-${appointment.id}",
                    title = "Consulta Agendada",
                    description = "Consulta com ${appointment.consultaType.displayName} para ${pet?.name ?: "Pet"}",
                    reminderType = ReminderType.APPOINTMENT,
                    priority = ReminderPriority.NORMAL,
                    dueDate = LocalDateTime.parse("${appointment.consultaDate}T${appointment.consultaTime}"),
                    petId = appointment.petId.toString(),
                    petName = pet?.name
                )
            )
        }

        // Lembretes de Medicamentos
        val medications = medicationRepository.findActiveMedications(ownerUUID)
        medications.forEach { medication ->
            if (medication.endDate.isAfter(LocalDate.now())) {
                val priority = if (medication.endDate.isBefore(today.plusDays(3))) {
                    ReminderPriority.HIGH
                } else {
                    ReminderPriority.NORMAL
                }

                reminders.add(
                    ReminderItem(
                        id = "medication-${medication.id}",
                        title = "Tratamento em Andamento",
                        description = "${medication.name} para ${getPetName(pets, medication.petId)}",
                        reminderType = ReminderType.MEDICATION,
                        priority = priority,
                        dueDate = medication.endDate,
                        petId = medication.petId.toString(),
                        petName = getPetName(pets, medication.petId)
                    )
                )
            }
        }

        // Adicionar lembretes automáticos baseados no tipo de pet
        pets.forEach { pet ->
            // Vermifugação (a cada 3 meses)
            val lastDeworming = vaccineRepository.findLastDewormingByPet(pet.id!!)
            if (shouldScheduleDeworming(lastDeworming, today)) {
                reminders.add(
                    ReminderItem(
                        id = "deworming-${pet.id}",
                        title = "Vermifugação",
                        description = "Está na hora da vermifugação de ${pet.name}",
                        reminderType = ReminderType.DEWORMING,
                        priority = ReminderPriority.NORMAL,
                        dueDate = today.plusDays(7).atStartOfDay(),
                        petId = pet.id.toString(),
                        petName = pet.name
                    )
                )
            }
        }

        val summary = createSummary(reminders, now, today)

        return ReminderResponse(
            reminders = reminders.sortedWith(
                compareByDescending<ReminderItem> { it.priority.level }
                    .thenBy { it.dueDate }
            ),
            summary = summary
        )
    }

    private fun calculateVaccinePriority(dueDate: LocalDateTime, today: LocalDate): ReminderPriority {
        val daysUntilDue = ChronoUnit.DAYS.between(today, dueDate.toLocalDate())
        return when {
            daysUntilDue < 0 -> ReminderPriority.CRITICAL  // Atrasada
            daysUntilDue <= 3 -> ReminderPriority.HIGH     // Vence em 3 dias
            daysUntilDue <= 7 -> ReminderPriority.NORMAL   // Vence em 1 semana
            else -> ReminderPriority.LOW                   // Ainda tem tempo
        }
    }

    private fun shouldScheduleDeworming(lastDeworming: LocalDate?, today: LocalDate): Boolean {
        if (lastDeworming == null) return true  // Nunca fez
        val monthsSince = ChronoUnit.MONTHS.between(lastDeworming, today)
        return monthsSince >= 3  // 3 meses é o período recomendado
    }

    private fun getPetName(pets: List<Pet>, petId: UUID): String {
        return pets.find { it.id == petId }?.name ?: "Pet"
    }

    private fun createSummary(reminders: List<ReminderItem>, now: LocalDateTime, today: LocalDate): ReminderSummary {
        val overdueReminders = reminders.count {
            it.dueDate.isBefore(LocalDate.now()) && !it.isCompleted
        }
        val todayReminders = reminders.count {
            it.dueDate.isEqual(today) && !it.isCompleted
        }
        val weekReminders = reminders.count {
            val daysUntil = ChronoUnit.DAYS.between(today, it.dueDate.atStartOfDay())
            daysUntil in 1..7 && !it.isCompleted
        }
        val monthReminders = reminders.count {
            val daysUntil = ChronoUnit.DAYS.between(today, it.dueDate.atStartOfDay())
            daysUntil in 8..30 && !it.isCompleted
        }
        val criticalReminders = reminders.count {
            it.priority == ReminderPriority.CRITICAL && !it.isCompleted
        }

        return ReminderSummary(
            totalReminders = reminders.size,
            overdueReminders = overdueReminders,
            todayReminders = todayReminders,
            weekReminders = weekReminders,
            monthReminders = monthReminders,
            criticalReminders = criticalReminders
        )
    }
}