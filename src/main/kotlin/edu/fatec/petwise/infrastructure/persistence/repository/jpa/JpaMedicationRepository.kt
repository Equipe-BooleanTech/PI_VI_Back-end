package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.MedicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaMedicationRepository: JpaRepository<MedicationEntity, UUID> {
    fun findByUserId(userId: UUID): List<MedicationEntity>
    fun findByPrescriptionId(prescriptionId: UUID): List<MedicationEntity>
}
