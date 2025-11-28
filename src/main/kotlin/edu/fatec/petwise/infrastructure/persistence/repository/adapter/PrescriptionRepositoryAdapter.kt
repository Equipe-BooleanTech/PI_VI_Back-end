package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaPrescriptionRepository
import edu.fatec.petwise.domain.entity.Prescription
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import edu.fatec.petwise.infrastructure.persistence.entity.PrescriptionEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PrescriptionRepositoryAdapter(
    private val repository: JpaPrescriptionRepository
) : PrescriptionRepository {

    override fun findAll(): List<Prescription> {
        return repository.findAll().map { it.toDomain() }
    }

    override fun findByPetId(petId: UUID): List<Prescription> {
        return repository.findByPetId(petId).map { it.toDomain() }
    }

    override fun findByUserId(userId: UUID): List<Prescription> {
        return repository.findByUserId(userId).map { it.toDomain() }
    }

    override fun findByVeterinaryId(veterinaryId: UUID): List<Prescription> {
        return repository.findByVeterinaryId(veterinaryId).map { it.toDomain() }
    }

    override fun findByVeterinaryIdAndPetId(veterinaryId: UUID, petId: UUID): List<Prescription> {
        return repository.findByVeterinaryIdAndPetId(veterinaryId, petId).map { it.toDomain() }
    }

    override fun findByIdAndUserId(id: UUID, userId: UUID): Prescription? {
        return repository.findByIdAndUserId(id, userId)?.toDomain()
    }

    override fun findById(id: UUID): Optional<Prescription> {
        return repository.findById(id).map { it.toDomain() }
    }

    override fun existsByPetIdAndVeterinaryIdNot(petId: UUID, veterinaryId: UUID): Boolean {
        return repository.existsByPetIdAndVeterinaryIdNot(petId, veterinaryId)
    }

    override fun existsByPetId(petId: UUID): Boolean {
        return repository.existsByPetId(petId)
    }

    override fun save(prescription: Prescription): Prescription {
        val entity = prescription.toEntity()
        return repository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    override fun deleteByPetId(petId: UUID) {
        repository.deleteByPetId(petId)
    }

    private fun PrescriptionEntity.toDomain(): Prescription {
        return Prescription(
            id = this.id,
            userId = this.userId,
            petId = this.petId,
            veterinaryId = this.veterinaryId,
            medicalRecordId = this.medicalRecordId,
            prescriptionDate = this.prescriptionDate,
            instructions = this.instructions,
            diagnosis = this.diagnosis,
            validUntil = this.validUntil,
            status = this.status,
            medications = this.medications,
            observations = this.observations,
            active = this.active,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun Prescription.toEntity(): PrescriptionEntity {
        return PrescriptionEntity(
            id = this.id,
            userId = this.userId,
            petId = this.petId,
            veterinaryId = this.veterinaryId,
            medicalRecordId = this.medicalRecordId,
            prescriptionDate = this.prescriptionDate,
            instructions = this.instructions,
            diagnosis = this.diagnosis,
            validUntil = this.validUntil,
            status = this.status,
            medications = this.medications,
            observations = this.observations,
            active = this.active,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
