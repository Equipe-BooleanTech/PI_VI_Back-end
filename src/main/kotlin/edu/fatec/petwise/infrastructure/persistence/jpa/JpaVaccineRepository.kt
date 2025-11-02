package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.VaccineEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface JpaVaccineRepository : JpaRepository<VaccineEntity, UUID> {
    fun findByPetId(petId: UUID): List<VaccineEntity>

    @Query("SELECT v FROM VaccineEntity v WHERE v.petId = :petId AND v.nextDoseDate IS NOT NULL AND v.nextDoseDate <= :today")
    fun findDueVaccinesByPetIdAndDate(petId: UUID, today: LocalDate): List<VaccineEntity>

    // Novos métodos para Dashboard
    @Query("""
        SELECT COUNT(v) FROM VaccineEntity v 
        WHERE v.petId IN (
            SELECT p.id FROM PetEntity p WHERE p.ownerId = :ownerId
        ) AND (v.nextDoseDate IS NOT NULL AND v.nextDoseDate >= :today)
    """)
    fun countPendingVaccines(ownerId: UUID, today: LocalDate = LocalDate.now()): Int

    @Query("SELECT v FROM VaccineEntity v WHERE v.petId = :petId AND (v.nextDoseDate IS NOT NULL AND v.nextDoseDate >= :today)")
    fun findPendingVaccinesByPet(petId: UUID, today: LocalDate = LocalDate.now()): List<VaccineEntity>

    @Query("SELECT MAX(v.applicationDate) FROM VaccineEntity v WHERE v.petId = :petId AND v.vaccineType = 'DEWORMING'")
    fun findLastDewormingByPet(petId: UUID): LocalDate?

    @Query("SELECT v FROM VaccineEntity v WHERE v.petId = :petId AND v.vaccineType = 'DEWORMING' ORDER BY v.dateApplied DESC LIMIT 1")
    fun findLatestDewormingByPet(petId: UUID): VaccineEntity?
}