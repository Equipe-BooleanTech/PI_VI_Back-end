package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.LabScheduleDTO
import edu.fatec.petwise.domain.entity.LabSchedule
import edu.fatec.petwise.domain.repository.LabScheduleRepository
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.util.UUID
import java.time.LocalDateTime
import java.time.LocalDate
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageLabSchedulesUseCase @Autowired constructor(
    private val labScheduleRepository: LabScheduleRepository
) {
    private val logger = LoggerFactory.getLogger(ManageLabSchedulesUseCase::class.java)

    fun getAllSchedules(page: Int = 0, size: Int = 20): List<LabScheduleDTO> {
        return try {
            val pageable = PageRequest.of(page, size)
            labScheduleRepository.findAll(pageable).content.map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar todos os agendamentos: ${e.message}")
            throw RuntimeException("Falha ao recuperar agendamentos", e)
        }
    }

    fun getSchedulesByPetId(petId: UUID): List<LabScheduleDTO> {
        return try {
            labScheduleRepository.findByPetIdOrderByScheduleDateDesc(petId).map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar agendamentos por pet $petId: ${e.message}")
            throw RuntimeException("Falha ao recuperar agendamentos do pet", e)
        }
    }


    fun getSchedulesByVeterinarianId(veterinarianId: UUID, date: LocalDate): List<LabScheduleDTO> {
        return try {
            labScheduleRepository.findByVeterinarianIdAndScheduleDate(veterinarianId, date).map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar agendamentos do veterinário $veterinarianId para $date: ${e.message}")
            throw RuntimeException("Falha ao recuperar agenda do veterinário", e)
        }
    }

    fun createSchedule(dto: LabScheduleDTO): LabScheduleDTO {
        return try {
            logger.info("Criando novo agendamento: ${dto.petId} - ${dto.testName}")
            
            // Validação de conflitos de horário
            val existingSchedules = labScheduleRepository.findByScheduleDateAndStatus(
                dto.scheduleDate.toLocalDate(), 
                "CONFIRMED"
            )
            
            val conflict = existingSchedules.any { schedule ->
                schedule.appointmentTime.plusMinutes(schedule.duration.toLong()) > dto.appointmentTime &&
                dto.appointmentTime.plusMinutes(dto.duration.toLong()) > schedule.appointmentTime
            }
            
            if (conflict) {
                throw IllegalArgumentException("Conflito de horário detectado para o agendamento")
            }
            
            val entity = dto.toEntity()
            val saved = labScheduleRepository.save(entity)
            logger.info("Agendamento criado com sucesso: ${saved.id}")
            
            saved.toDTO()
        } catch (e: IllegalArgumentException) {
            logger.error("Erro de validação ao criar agendamento: ${e.message}")
            throw e
        } catch (e: Exception) {
            logger.error("Erro ao criar agendamento: ${e.message}")
            throw RuntimeException("Falha ao criar agendamento", e)
        }
    }

    fun updateSchedule(id: UUID, dto: LabScheduleDTO): LabScheduleDTO {
        return try {
            logger.info("Atualizando agendamento: $id")
            
            val existing = labScheduleRepository.findById(id)
                .orElseThrow { NoSuchElementException("Agendamento não encontrado: $id") }
            
            // Validar se pode ser atualizado
            if (existing.status in listOf("COMPLETED", "CANCELLED", "NO_SHOW")) {
                throw IllegalArgumentException("Agendamento não pode ser alterado no status atual")
            }
            
            val updated = existing.copy(
                petId = dto.petId,
                testId = dto.testId,
                veterinarianId = dto.veterinarianId,
                scheduleDate = dto.scheduleDate,
                appointmentTime = dto.appointmentTime,
                duration = dto.duration,
                priority = dto.priority,
                notes = dto.notes,
                updatedAt = java.time.LocalDateTime.now()
            )
            
            val saved = labScheduleRepository.save(updated)
            logger.info("Agendamento atualizado com sucesso: ${saved.id}")
            
            saved.toDTO()
        } catch (e: NoSuchElementException) {
            logger.error("Agendamento não encontrado para atualização: $id")
            throw e
        } catch (e: IllegalArgumentException) {
            logger.error("Erro de validação ao atualizar agendamento $id: ${e.message}")
            throw e
        } catch (e: Exception) {
            logger.error("Erro ao atualizar agendamento $id: ${e.message}")
            throw RuntimeException("Falha ao atualizar agendamento", e)
        }
    }

    fun cancelSchedule(id: UUID): Boolean {
        return try {
            logger.info("Cancelando agendamento: $id")
            
            val existing = labScheduleRepository.findById(id)
                .orElseThrow { NoSuchElementException("Agendamento não encontrado: $id") }
            
            if (existing.status == "CANCELLED") {
                throw IllegalArgumentException("Agendamento já está cancelado")
            }
            
            val cancelled = existing.copy(
                status = "CANCELLED",
                updatedAt = java.time.LocalDateTime.now()
            )
            
            labScheduleRepository.save(cancelled)
            logger.info("Agendamento cancelado com sucesso: $id")
            true
        } catch (e: NoSuchElementException) {
            logger.error("Agendamento não encontrado para cancelamento: $id")
            throw e
        } catch (e: IllegalArgumentException) {
            logger.error("Erro de validação ao cancelar agendamento $id: ${e.message}")
            throw e
        } catch (e: Exception) {
            logger.error("Erro ao cancelar agendamento $id: ${e.message}")
            throw RuntimeException("Falha ao cancelar agendamento", e)
        }
    }

    fun getScheduleByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<LabScheduleDTO> {
        return try {
            labScheduleRepository.findByScheduleDateBetween(startDate, endDate).map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar agendamentos por período: ${e.message}")
            throw RuntimeException("Falha ao buscar agendamentos", e)
        }
    }


    fun getTodaySchedules(): List<LabScheduleDTO> {
        return try {
            val today = LocalDate.now()
            val startOfDay = today.atStartOfDay()
            val endOfDay = today.atTime(23, 59, 59)
            
            labScheduleRepository.findByScheduleDateBetween(startOfDay, endOfDay).map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar agendamentos de hoje: ${e.message}")
            throw RuntimeException("Falha ao recuperar agenda de hoje", e)
        }
    }

    fun getUpcomingSchedules(days: Int = 7): List<LabScheduleDTO> {
        return try {
            val today = LocalDateTime.now()
            val endDate = today.plusDays(days.toLong())
            
            labScheduleRepository.findByScheduleDateBetween(today, endDate)
                .filter { it.status in listOf("SCHEDULED", "CONFIRMED") }
                .map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar próximos agendamentos: ${e.message}")
            throw RuntimeException("Falha ao recuperar próximos agendamentos", e)
        }
    }

    fun updateScheduleStatus(id: UUID, status: String): LabScheduleDTO {
        return try {
            logger.info("Atualizando status do agendamento $id para: $status")
            
            val existing = labScheduleRepository.findById(id)
                .orElseThrow { NoSuchElementException("Agendamento não encontrado: $id") }
            
            if (!isValidStatusTransition(existing.status, status)) {
                throw IllegalArgumentException("Transição de status inválida: ${existing.status} -> $status")
            }
            
            val updated = existing.copy(
                status = status,
                updatedAt = java.time.LocalDateTime.now()
            )
            
            // Definir completed_at se estiver completando
            if (status == "COMPLETED") {
                updated.completedAt = java.time.LocalDateTime.now()
            }
            
            val saved = labScheduleRepository.save(updated)
            logger.info("Status do agendamento atualizado: ${saved.id}")
            
            saved.toDTO()
        } catch (e: NoSuchElementException) {
            logger.error("Agendamento não encontrado: $id")
            throw e
        } catch (e: IllegalArgumentException) {
            logger.error("Erro de validação ao atualizar status: ${e.message}")
            throw e
        } catch (e: Exception) {
            logger.error("Erro ao atualizar status do agendamento $id: ${e.message}")
            throw RuntimeException("Falha ao atualizar status", e)
        }
    }

    fun getScheduleStatistics(): Map<String, Any> {
        return try {
            logger.info("Gerando estatísticas de agendamentos")
            
            val allSchedules = labScheduleRepository.findAll()
            val total = allSchedules.size
            
            val statusStats = allSchedules.groupBy { it.status }
                .mapValues { (_, schedules) -> schedules.size }
            
            val priorityStats = allSchedules.groupBy { it.priority }
                .mapValues { (_, schedules) -> schedules.size }
            
            val thisMonth = allSchedules.filter {
                it.scheduleDate.month == java.time.LocalDateTime.now().month
            }
            
            val monthlyStats = mapOf(
                "total_this_month" to thisMonth.size,
                "completed_this_month" to thisMonth.count { it.status == "COMPLETED" },
                "cancelled_this_month" to thisMonth.count { it.status == "CANCELLED" }
            )
            
            mapOf(
                "total_schedules" to total,
                "status_distribution" to statusStats,
                "priority_distribution" to priorityStats,
                "monthly_stats" to monthlyStats,
                "completion_rate" to if (total > 0) (statusStats["COMPLETED"] ?: 0).toDouble() / total * 100 else 0.0,
                "cancellation_rate" to if (total > 0) (statusStats["CANCELLED"] ?: 0).toDouble() / total * 100 else 0.0
            )
        } catch (e: Exception) {
            logger.error("Erro ao gerar estatísticas: ${e.message}")
            throw RuntimeException("Falha ao gerar estatísticas", e)
        }
    }

    private fun isValidStatusTransition(currentStatus: String, newStatus: String): Boolean {
        val validTransitions = mapOf(
            "SCHEDULED" to setOf("CONFIRMED", "CANCELLED", "NO_SHOW"),
            "CONFIRMED" to setOf("IN_PROGRESS", "CANCELLED", "NO_SHOW"),
            "IN_PROGRESS" to setOf("COMPLETED", "CANCELLED"),
            "COMPLETED" to setOf(), // Status final
            "CANCELLED" to setOf(), // Status final
            "NO_SHOW" to setOf() // Status final
        )
        
        return validTransitions[currentStatus]?.contains(newStatus) ?: false
    }


    private fun LabSchedule.toDTO(): LabScheduleDTO {
        return LabScheduleDTO.fromEntity(this)
    }

    private fun LabScheduleDTO.toEntity(): LabSchedule {
        return LabScheduleDTO.toEntity()
    }
}
