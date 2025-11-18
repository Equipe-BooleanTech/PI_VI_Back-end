package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.PrescriptionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaPrescriptionRepository: JpaRepository<PrescriptionEntity, UUID> {
    fun findByPetId(petId: UUID): List<PrescriptionEntity>
    fun findByUserId(userId: UUID): List<PrescriptionEntity>
    fun findByIdAndUserId(id: UUID, userId: UUID): PrescriptionEntity?
    fun deleteByPetId(petId: UUID)
}
