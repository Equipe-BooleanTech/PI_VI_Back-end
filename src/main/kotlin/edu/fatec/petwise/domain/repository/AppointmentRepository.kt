package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Appointment
import edu.fatec.petwise.domain.entity.AppointmentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface AppointmentRepository : JpaRepository<Appointment, UUID> {

    fun findByOwnerIdOrderByAppointmentDatetimeDesc(ownerId: UUID): List<Appointment>
    fun findByOwnerIdAndStatus(ownerId: UUID, status: AppointmentStatus): List<Appointment>
    fun findByVeterinaryIdOrderByAppointmentDatetimeDesc(veterinaryId: UUID): List<Appointment>
    fun findByPetIdOrderByAppointmentDatetimeDesc(petId: UUID): List<Appointment>
    fun findByIdAndOwnerId(id: UUID, ownerId: UUID): Appointment?
    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.ownerId = :ownerId 
        AND a.appointmentDatetime > :now 
        AND a.status IN ('AGENDADA', 'CONFIRMADA')
        ORDER BY a.appointmentDatetime ASC
    """)
    fun findUpcomingAppointments(ownerId: UUID, now: LocalDateTime): List<Appointment>
    @Query("""
        SELECT a FROM Appointment a 
        WHERE a.ownerId = :ownerId 
        AND a.appointmentDatetime < :now 
        ORDER BY a.appointmentDatetime DESC
    """)
    fun findPastAppointments(ownerId: UUID, now: LocalDateTime): List<Appointment>

    fun countByOwnerIdAndStatus(ownerId: UUID, status: AppointmentStatus): Long

    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a 
        WHERE a.veterinaryId = :veterinaryId 
        AND a.status NOT IN ('CANCELADA', 'NAO_COMPARECEU')
        AND a.appointmentDatetime >= :startTime 
        AND a.appointmentDatetime < :endTime
    """)
    fun existsConflictingAppointment(
        veterinaryId: UUID, 
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): Boolean
}
