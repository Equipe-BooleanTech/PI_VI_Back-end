package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.MedicalRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface MedicalRecordRepository : JpaRepository<MedicalRecord, UUID> {
    
    fun findByPetIdAndActiveTrueOrderByRecordDateDesc(petId: UUID): List<MedicalRecord>
    
    fun findByUserIdAndActiveTrue(userId: UUID): List<MedicalRecord>
    
    fun findByIdAndUserIdAndActiveTrue(id: UUID, userId: UUID): MedicalRecord?
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.veterinarian = :veterinarian AND mr.active = true ORDER BY mr.recordDate DESC")
    fun findByVeterinarian(@Param("veterinarian") veterinarian: String): List<MedicalRecord>
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.petId = :petId AND mr.veterinarian = :veterinarian AND mr.active = true ORDER BY mr.recordDate DESC")
    fun findByPetIdAndVeterinarian(@Param("petId") petId: UUID, @Param("veterinarian") veterinarian: String): List<MedicalRecord>
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointmentId = :appointmentId AND mr.active = true")
    fun findByAppointmentId(@Param("appointmentId") appointmentId: UUID): MedicalRecord?
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.petId = :petId AND mr.recordDate BETWEEN :startDate AND :endDate AND mr.active = true ORDER BY mr.recordDate DESC")
    fun findByPetIdAndRecordDateBetween(
        @Param("petId") petId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<MedicalRecord>
    
    @Query("SELECT COUNT(mr) FROM MedicalRecord mr WHERE mr.petId = :petId AND mr.active = true")
    fun countByPetIdAndActiveTrue(@Param("petId") petId: UUID): Long

    @Query("SELECT DISTINCT mr.petId FROM MedicalRecord mr WHERE mr.veterinarian = :veterinarian AND mr.active = true")
    fun findDistinctPetIdsByVeterinarian(@Param("veterinarian") veterinarian: String): List<UUID>


}