package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.MedicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaMedicationRepository : JpaRepository<MedicationEntity, UUID> {
    fun findByPetId(petId: UUID): List<MedicationEntity>
    fun findByPetIdAndActive(petId: UUID, active: Boolean): List<MedicationEntity>

    @Query("""
        SELECT m FROM MedicationEntity m 
        WHERE m.petId IN (
            SELECT p.id FROM PetEntity p WHERE p.ownerId = :ownerId
        ) 
        AND m.active = true 
        AND (m.endDate IS NULL OR m.endDate >= :now)
    """)
    fun findActiveMedications(ownerId: UUID, now: LocalDateTime = LocalDateTime.now()): List<MedicationEntity>
}