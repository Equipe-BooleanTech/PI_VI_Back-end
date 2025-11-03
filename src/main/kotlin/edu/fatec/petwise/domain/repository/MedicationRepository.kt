package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Medication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface MedicationRepository : JpaRepository<Medication, Long> {
    
    fun findByPetIdAndActiveTrueOrderByCreatedAtDesc(petId: UUID): List<Medication>
    
    fun findByPrescriptionIdAndActiveTrue(prescriptionId: UUID): List<Medication>
    
    fun findByUserIdAndActiveTrue(userId: UUID): List<Medication>
    
    fun findByIdAndUserIdAndActiveTrue(id: UUID, userId: UUID): Medication?
    
    fun findByPetIdAndPrescriptionIdAndActiveTrue(petId: UUID, prescriptionId: UUID): List<Medication>
    
    @Query("SELECT m FROM Medication m WHERE m.petId = :petId AND m.administered = :administered AND m.active = true ORDER BY m.createdAt DESC")
    fun findByPetIdAndAdministered(@Param("petId") petId: UUID, @Param("administered") administered: Boolean): List<Medication>
    
    @Query("SELECT m FROM Medication m WHERE m.petId = :petId AND (m.endDate IS NULL OR m.endDate >= :currentDate) AND m.administered = false AND m.active = true")
    fun findActiveByPetId(@Param("petId") petId: Long, @Param("currentDate") currentDate: LocalDateTime): List<Medication>
    
    @Query("SELECT m FROM Medication m WHERE m.petId = :petId AND m.medicationName LIKE %:medicationName% AND m.active = true")
    fun findByPetIdAndMedicationNameContaining(@Param("petId") petId: UUID, @Param("medicationName") medicationName: String): List<Medication>
    
    @Query("SELECT COUNT(m) FROM Medication m WHERE m.petId = :petId AND m.active = true")
    fun countByPetIdAndActiveTrue(@Param("petId") petId: Long): Long
    
    @Query("SELECT COUNT(m) FROM Medication m WHERE m.petId = :petId AND m.administered = true AND m.active = true")
    fun countByPetIdAndAdministeredTrue(@Param("petId") petId: Long): Long
}