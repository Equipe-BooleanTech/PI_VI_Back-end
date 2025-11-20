package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.MedicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaMedicationRepository: JpaRepository<MedicationEntity, UUID> {
    fun findByUserId(userId: UUID): List<MedicationEntity>
    fun findByPrescriptionId(prescriptionId: UUID): List<MedicationEntity>
    fun deleteByPrescriptionId(prescriptionId: UUID)

    @Query("SELECT m FROM MedicationEntity m JOIN PrescriptionEntity p ON m.prescriptionId = p.id WHERE p.petId = :petId")
    fun findByPetId(@Param("petId") petId: UUID): List<MedicationEntity>
}
