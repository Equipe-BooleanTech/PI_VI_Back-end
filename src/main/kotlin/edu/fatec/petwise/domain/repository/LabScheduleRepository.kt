package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.LabSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Repository
interface LabScheduleRepository : JpaRepository<LabSchedule, UUID> {

    fun findByPetIdOrderByScheduleDateDesc(petId: UUID): List<LabSchedule>

    fun findByTestIdOrderByScheduleDateDesc(testId: UUID): List<LabSchedule>

    fun findByVeterinarianIdAndScheduleDate(
        veterinarian_id: UUID, scheduleDate: LocalDateTime
    ): List<LabSchedule>

    fun findByStatus(status: String): List<LabSchedule>

    fun findByScheduleDateBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<LabSchedule>

    fun findByPriority(priority: String): List<LabSchedule>

    fun findByScheduleDateAndStatus(
        scheduleDate: LocalDate, status: String
    ): List<LabSchedule>

    fun countByStatusAndScheduleDate(
        status: String, scheduleDate: LocalDateTime
    ): Long

    @Query("SELECT ls FROM LabSchedule ls WHERE CAST(ls.scheduleDate AS LocalDate) = :date ORDER BY ls.scheduleDate ASC")
    fun findByScheduleDate(
        @Param("date") date: LocalDate
    ): List<LabSchedule>

    @Query("""
        SELECT 
            ls.status,
            COUNT(*) as total,
            SUM(CASE WHEN ls.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed,
            SUM(CASE WHEN ls.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled
        FROM LabSchedule ls 
        WHERE ls.scheduleDate BETWEEN :startDate AND :endDate
        GROUP BY ls.status
    """)
    fun getAttendanceStats(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>
}