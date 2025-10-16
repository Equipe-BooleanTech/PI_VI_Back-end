package edu.fatec.petwise.infrastructure.persistence.adapter

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.infrastructure.persistence.entity.AppointmentEntity
import edu.fatec.petwise.infrastructure.persistence.jpa.JpaAppointmentRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class AppointmentRepositoryAdapter(
    private val jpaAppointmentRepository: JpaAppointmentRepository
) : AppointmentRepository {

    override fun save(appointment: Appointment): Appointment {
        val entity = appointment.toEntity()
        val saved = jpaAppointmentRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): Appointment? {
        return jpaAppointmentRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Appointment> {
        return jpaAppointmentRepository.findAll().map { it.toDomain() }
    }

    override fun findByPetId(petId: UUID): List<Appointment> {
        return jpaAppointmentRepository.findByPetId(petId).map { it.toDomain() }
    }

    override fun findByOwnerId(ownerId: UUID): List<Appointment> {
        return jpaAppointmentRepository.findByOwnerId(ownerId).map { it.toDomain() }
    }

    override fun findByStatus(status: ConsultaStatus): List<Appointment> {
        return jpaAppointmentRepository.findByStatus(status).map { it.toDomain() }
    }

    override fun findByConsultaDateBetween(startDate: String, endDate: String): List<Appointment> {
        return jpaAppointmentRepository.findByConsultaDate(startDate).map { it.toDomain() }
    }

    override fun existsById(id: UUID): Boolean {
        return jpaAppointmentRepository.existsById(id)
    }

    override fun update(appointment: Appointment): Appointment {
        val entity = appointment.toEntity()
        val saved = jpaAppointmentRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: UUID) {
        jpaAppointmentRepository.deleteById(id)
    }

    private fun Appointment.toEntity() = AppointmentEntity(
        id = this.id,
        petId = this.petId,
        ownerId = this.ownerId,
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

    private fun AppointmentEntity.toDomain() = Appointment(
        id = this.id,
        petId = this.petId,
        ownerId = this.ownerId,
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
