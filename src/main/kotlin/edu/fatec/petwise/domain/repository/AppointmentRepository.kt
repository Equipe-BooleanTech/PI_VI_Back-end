package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.enums.ConsultaStatus
import java.time.LocalDateTime
import java.util.UUID

interface AppointmentRepository {
    fun save(appointment: Appointment): Appointment
    fun findById(id: UUID): Appointment?
    fun findAll(): List<Appointment>
    fun findByPetId(petId: UUID): List<Appointment>
    fun findByOwnerId(ownerId: UUID): List<Appointment>
    fun findByStatus(status: ConsultaStatus): List<Appointment>
    fun findByConsultaDateBetween(startDate: String, endDate: String): List<Appointment>
    fun existsById(id: UUID): Boolean
    fun update(appointment: Appointment): Appointment
    fun delete(id: UUID)
}
