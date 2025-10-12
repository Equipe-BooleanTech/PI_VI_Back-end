package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.entity.AppointmentStatus
import java.time.LocalDateTime
import java.util.UUID

interface AppointmentRepository {
    fun save(appointment: Appointment): Appointment
    fun findById(id: UUID): Appointment?
    fun findAll(): List<Appointment>
    fun findByPetId(petId: UUID): List<Appointment>
    fun findByOwnerId(ownerId: UUID): List<Appointment>
    fun findByVeterinaryId(veterinaryId: UUID): List<Appointment>
    fun findByStatus(status: AppointmentStatus): List<Appointment>
    fun findByScheduledDateBetween(start: LocalDateTime, end: LocalDateTime): List<Appointment>
    fun existsById(id: UUID): Boolean
    fun update(appointment: Appointment): Appointment
    fun delete(id: UUID)
}
