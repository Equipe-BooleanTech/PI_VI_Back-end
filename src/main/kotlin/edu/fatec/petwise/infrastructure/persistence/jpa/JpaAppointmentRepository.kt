package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.infrastructure.persistence.entity.AppointmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaAppointmentRepository : JpaRepository<AppointmentEntity, UUID> {
    fun findByPetId(petId: UUID): List<AppointmentEntity>
    fun findByOwnerId(ownerId: UUID): List<AppointmentEntity>
    fun findByStatus(status: ConsultaStatus): List<AppointmentEntity>
    fun findByConsultaDate(consultaDate: String): List<AppointmentEntity>

    // Novos métodos para Dashboard
    @Query("""
        SELECT COUNT(a) FROM AppointmentEntity a 
        WHERE a.ownerId = :ownerId 
        AND a.status IN ('SCHEDULED', 'RESCHEDULED')
        AND (a.consultaDate || ' ' || a.consultaTime) BETWEEN :fromDate AND :toDate
    """)
    fun countUpcomingAppointments(
        ownerId: UUID,
        fromDate: LocalDateTime,
        toDate: LocalDateTime
    ): Int

    @Query("""
        SELECT COUNT(a) FROM AppointmentEntity a 
        WHERE a.ownerId = :ownerId 
        AND (a.consultaDate || ' ' || a.consultaTime) >= :fromDate
    """)
    fun countRecentAppointments(
        ownerId: UUID,
        fromDate: LocalDateTime
    ): Int

    @Query("""
        SELECT a FROM AppointmentEntity a 
        WHERE a.ownerId = :ownerId 
        AND a.status IN ('SCHEDULED', 'RESCHEDULED')
        AND (a.consultaDate || ' ' || a.consultaTime) BETWEEN :fromDate AND :toDate
        ORDER BY a.consultaDate, a.consultaTime
    """)
    fun findUpcomingAppointments(
        ownerId: UUID,
        fromDate: LocalDateTime,
        toDate: LocalDateTime
    ): List<AppointmentEntity>
}