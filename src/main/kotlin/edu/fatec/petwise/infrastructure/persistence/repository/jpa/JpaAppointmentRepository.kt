package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.AppointmentEntity
import edu.fatec.petwise.domain.enums.ConsultaStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaAppointmentRepository : JpaRepository<AppointmentEntity, UUID> {
    fun findByPetId(petId: UUID): List<AppointmentEntity>
    fun findByOwnerId(ownerId: UUID): List<AppointmentEntity>
    fun findByStatus(status: ConsultaStatus): List<AppointmentEntity>
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): AppointmentEntity?
}
