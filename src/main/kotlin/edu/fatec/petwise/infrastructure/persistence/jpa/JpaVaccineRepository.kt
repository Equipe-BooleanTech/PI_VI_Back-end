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
    
    @Query("SELECT v FROM VaccineEntity v JOIN PetEntity p ON v.petId = p.id WHERE p.ownerId = :ownerId")
    fun findByOwnerId(ownerId: UUID): List<VaccineEntity>
    
    @Query("SELECT COUNT(v) FROM VaccineEntity v JOIN PetEntity p ON v.petId = p.id WHERE p.ownerId = :ownerId")
    fun countByOwnerId(ownerId: UUID): Int
    
    @Query("SELECT v FROM VaccineEntity v WHERE v.petId = :petId AND v.nextDoseDate IS NOT NULL AND v.nextDoseDate <= :today")
    fun findDueVaccinesByPetIdAndDate(petId: UUID, today: LocalDate): List<VaccineEntity>
}
