package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Prescription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface PrescriptionRepository : JpaRepository<Prescription, UUID> {
    
    fun findByPetIdAndActiveTrueOrderByPrescriptionDateDesc(petId: UUID): List<Prescription>
    
    fun findByUserIdAndActiveTrue(userId: UUID): List<Prescription>
    
    fun findByIdAndUserIdAndActiveTrue(id: UUID, userId: UUID): Prescription?
    
    fun findByPetIdAndStatusAndActiveTrue(petId: UUID, status: Prescription.PrescriptionStatus): List<Prescription>
    
    @Query("SELECT p FROM Prescription p WHERE p.petId = :petId AND p.veterinarian = :veterinarian AND p.active = true ORDER BY p.prescriptionDate DESC")
    fun findByPetIdAndVeterinarian(@Param("petId") petId: UUID, @Param("veterinarian") veterinarian: String): List<Prescription>
    
    @Query("SELECT p FROM Prescription p WHERE p.userId = :userId AND p.status = :status AND p.active = true ORDER BY p.prescriptionDate DESC")
    fun findByUserIdAndStatus(@Param("userId") userId: UUID, @Param("status") status: Prescription.PrescriptionStatus): List<Prescription>
    
    @Query("SELECT p FROM Prescription p WHERE p.petId = :petId AND p.validUntil IS NOT NULL AND p.validUntil < :currentDate AND p.active = true")
    fun findExpiredByPetId(@Param("petId") petId: UUID, @Param("currentDate") currentDate: LocalDateTime): List<Prescription>
    
    @Query("SELECT p FROM Prescription p WHERE p.petId = :petId AND (p.validUntil IS NULL OR p.validUntil >= :currentDate) AND p.status = 'ATIVA' AND p.active = true")
    fun findActiveByPetId(@Param("petId") petId: UUID, @Param("currentDate") currentDate: LocalDateTime): List<Prescription>
    
    @Query("SELECT p FROM Prescription p WHERE p.veterinarian = :veterinarian AND p.active = true ORDER BY p.prescriptionDate DESC")
    fun findByVeterinarian(@Param("veterinarian") veterinarian: String): List<Prescription>
    
    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.petId = :petId AND p.active = true")
    fun countByPetIdAndActiveTrue(@Param("petId") petId: UUID): Long
}