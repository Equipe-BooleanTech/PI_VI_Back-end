package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.enums.ConsultaStatus
import java.util.Optional
import java.util.UUID

interface AppointmentRepository {
    fun findAll(): List<Appointment>
    fun findById(id: UUID): Optional<Appointment>
    fun findByPetId(petId: UUID): List<Appointment>
    fun findByOwnerId(ownerId: UUID): List<Appointment>
    fun findByStatus(status: ConsultaStatus): List<Appointment>
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): Appointment?
    fun save(appointment: Appointment): Appointment
    fun deleteById(id: UUID)
}
