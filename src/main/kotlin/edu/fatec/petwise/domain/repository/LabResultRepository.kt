package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.LabResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface LabResultRepository : JpaRepository<LabResult, UUID> {

    fun findByPetIdOrderByResultDateDesc(petId: UUID): List<LabResult>

    fun findByTestIdOrderByResultDateDesc(testId: UUID): List<LabResult>

    fun findByStatus(status: String): List<LabResult>

    fun findByVeterinarianIdOrderByResultDateDesc(veterinarianId: UUID): List<LabResult>

    fun findByResultDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<LabResult>

    fun findByPetIdAndTestId(petId: UUID, testId: UUID): List<LabResult>

    fun countByStatus(status: String): Long

    @Query("SELECT lr.labTest.category, COUNT(lr) as total, AVG(CASE WHEN lr.status = 'NORMAL' THEN 1.0 ELSE 0.0 END) * 100 as normalPercentage FROM LabResult lr GROUP BY lr.labTest.category ORDER BY total DESC")
    fun getStatisticsByTestCategory(): List<Array<Any>>

    @Query("SELECT lr FROM LabResult lr WHERE lr.status = 'ABNORMAL' ORDER BY lr.resultDate DESC")
    fun findAbnormalResults(): List<LabResult>

    @Query("SELECT lr FROM LabResult lr WHERE lr.status = :status AND lr.resultDate BETWEEN :startDate AND :endDate ORDER BY lr.resultDate DESC")
    fun findByStatusAndDateRange(
        @Param("status") status: String,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<LabResult>
}