package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.LabSample
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID


@Repository
interface LabSampleRepository : JpaRepository<LabSample, UUID> {

    fun findByPetIdOrderByCollectionDateDesc(petId: UUID): List<LabSample>

    fun findByTestIdOrderByCollectionDateDesc(testId: UUID): List<LabSample>
    fun findByStatus(status: String): List<LabSample>

    fun findByTechnicianIdOrderByCollectionDateDesc(technicianId: UUID): List<LabSample>

    fun findByCollectionDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<LabSample>

    fun findByExpirationDateBefore(expirationDate: LocalDateTime): List<LabSample>

    fun findBySampleType(sampleType: String): List<LabSample>

    fun countByStatus(status: String): Long

    @Query("SELECT ls FROM LabSample ls WHERE ls.expirationDate IS NOT NULL AND ls.expirationDate < :currentDate")
    fun findExpiredSamples(@Param("currentDate") currentDate: LocalDateTime): List<LabSample>

    @Query("SELECT ls FROM LabSample ls WHERE ls.status = :status AND ls.collectionDate >= :startDate AND ls.collectionDate <= :endDate")
    fun findByStatusAndCollectionDateBetween(
        @Param("status") status: String,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<LabSample>

    @Query("SELECT ls FROM LabSample ls WHERE ls.technician.id = :technicianId AND ls.status = :status")
    fun findByTechnicianIdAndStatus(
        @Param("technicianId") technicianId: UUID,
        @Param("status") status: String
    ): List<LabSample>

    @Query("SELECT ls FROM LabSample ls WHERE ls.expirationDate IS NOT NULL AND ls.expirationDate <= :futureDate")
    fun findExpiringSamples(@Param("futureDate") futureDate: LocalDateTime): List<LabSample>
}