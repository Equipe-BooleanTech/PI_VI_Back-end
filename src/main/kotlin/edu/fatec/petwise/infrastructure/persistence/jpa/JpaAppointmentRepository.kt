package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.domain.entity.AppointmentStatus
import edu.fatec.petwise.infrastructure.persistence.entity.AppointmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaAppointmentRepository : JpaRepository<AppointmentEntity, UUID> {
    fun findByPetId(petId: UUID): List<AppointmentEntity>
    fun findByOwnerId(ownerId: UUID): List<AppointmentEntity>
    fun findByVeterinaryId(veterinaryId: UUID): List<AppointmentEntity>
    fun findByStatus(status: AppointmentStatus): List<AppointmentEntity>
    fun findByScheduledDateBetween(start: LocalDateTime, end: LocalDateTime): List<AppointmentEntity>
}
