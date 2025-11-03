package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.Vaccine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface VaccineRepository : JpaRepository<Vaccine, UUID> {
    
    fun findByPetIdAndActiveTrueOrderByVaccinationDateDesc(petId: UUID): List<Vaccine>
    
    fun findByUserIdAndActiveTrue(userId: UUID): List<Vaccine>
    
    fun findByIdAndUserIdAndActiveTrue(id: UUID, userId: UUID): Vaccine?
    
    fun findByPetIdAndVaccineTypeIdAndActiveTrue(petId: UUID, vaccineTypeId: UUID): List<Vaccine>
    
    @Query("SELECT v FROM Vaccine v WHERE v.petId = :petId AND v.vaccineTypeId = :vaccineTypeId AND v.active = true ORDER BY v.vaccinationDate DESC")
    fun findByPetIdAndVaccineTypeId(@Param("petId") petId: UUID, @Param("vaccineTypeId") vaccineTypeId: Long): List<Vaccine>
    
    @Query("SELECT v FROM Vaccine v WHERE v.petId = :petId AND (v.validUntil IS NULL OR v.validUntil >= :currentDate) AND v.active = true")
    fun findValidByPetId(@Param("petId") petId: UUID, @Param("currentDate") currentDate: LocalDate): List<Vaccine>
    
    @Query("SELECT v FROM Vaccine v WHERE v.petId = :petId AND v.veterinarian = :veterinarian AND v.active = true ORDER BY v.vaccinationDate DESC")
    fun findByPetIdAndVeterinarian(@Param("petId") petId: UUID, @Param("veterinarian") veterinarian: String): List<Vaccine>
    
    @Query("SELECT v FROM Vaccine v WHERE v.petId = :petId AND v.vaccinationDate >= :startDate AND v.vaccinationDate <= :endDate AND v.active = true ORDER BY v.vaccinationDate DESC")
    fun findByPetIdAndVaccinationDateBetween(
        @Param("petId") petId: UUID,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Vaccine>
    
    @Query("SELECT COUNT(v) FROM Vaccine v WHERE v.petId = :petId AND v.vaccineTypeId = :vaccineTypeId AND v.active = true")
    fun countByPetIdAndVaccineTypeId(@Param("petId") petId: UUID, @Param("vaccineTypeId") vaccineTypeId: UUID): Long
}