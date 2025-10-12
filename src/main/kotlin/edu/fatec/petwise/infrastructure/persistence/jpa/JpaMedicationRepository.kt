package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.MedicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaMedicationRepository : JpaRepository<MedicationEntity, UUID> {
    fun findByPetId(petId: UUID): List<MedicationEntity>
    fun findByPetIdAndActive(petId: UUID, active: Boolean): List<MedicationEntity>
}
