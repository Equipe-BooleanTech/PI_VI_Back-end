package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.infrastructure.persistence.entity.AppointmentEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaAppointmentRepository
import edu.fatec.petwise.domain.enums.ConsultaStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class AppointmentRepositoryAdapter(
    private val repository: JpaAppointmentRepository
): AppointmentRepository {
    override fun findAll(): List<Appointment> {
        return repository.findAll().map { it.toDomain() }
    }

    override fun findAll(pageable: Pageable): Page<Appointment> {
        return repository.findAll(pageable).map { it.toDomain() }
    }

    override fun findById(id: UUID): Optional<Appointment> = repository.findById(id).map { it.toDomain() }
    override fun findByPetId(petId: UUID): List<Appointment> = repository.findByPetId(petId).map { it.toDomain() }
    override fun findByOwnerId(ownerId: UUID): List<Appointment> = repository.findByOwnerId(ownerId).map { it.toDomain() }
    override fun findByStatus(status: ConsultaStatus): List<Appointment> = repository.findByStatus(status).map { it.toDomain() }

    override fun findByStatus(status: ConsultaStatus, pageable: Pageable): Page<Appointment> {
        return repository.findByStatus(status, pageable).map { it.toDomain() }
    }

    override fun findByIdAndOwnerId(id: UUID, ownerId: UUID): Appointment? = repository.findByIdAndOwnerId(id, ownerId)?.toDomain()
    override fun save(appointment: Appointment): Appointment {
        val entity = AppointmentEntity(
            petId = appointment.petId,
            ownerId = appointment.ownerId,
            petName = appointment.petName,
            veterinarianName = appointment.veterinarianName,
            consultaType = appointment.consultaType,
            consultaDate = appointment.consultaDate,
            consultaTime = appointment.consultaTime,
            status = appointment.status,
            symptoms = appointment.symptoms,
            diagnosis = appointment.diagnosis,
            treatment = appointment.treatment,
            prescriptions = appointment.prescriptions,
            notes = appointment.notes,
            nextAppointment = appointment.nextAppointment,
            price = appointment.price,
            isPaid = appointment.isPaid,
            createdAt = appointment.createdAt,
            updatedAt = appointment.updatedAt
        ).apply { id = appointment.id }
        val saved = repository.save(entity)
        return saved.toDomain()
    }
    override fun deleteById(id: UUID) = repository.deleteById(id)

    private fun AppointmentEntity.toDomain(): Appointment = Appointment(
        id = this.id,
        petId = this.petId,
        ownerId = this.ownerId,
        petName = this.petName,
        veterinarianName = this.veterinarianName,
        consultaType = this.consultaType,
        consultaDate = this.consultaDate,
        consultaTime = this.consultaTime,
        status = this.status,
        symptoms = this.symptoms,
        diagnosis = this.diagnosis,
        treatment = this.treatment,
        prescriptions = this.prescriptions,
        notes = this.notes,
        nextAppointment = this.nextAppointment,
        price = this.price,
        isPaid = this.isPaid,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
