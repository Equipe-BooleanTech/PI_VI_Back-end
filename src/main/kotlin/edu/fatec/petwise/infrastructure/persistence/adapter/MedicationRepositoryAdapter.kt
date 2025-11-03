package edu.fatec.petwise.infrastructure.persistence.adapter

import edu.fatec.petwise.domain.entity.Medication
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.infrastructure.persistence.entity.MedicationEntity
import edu.fatec.petwise.infrastructure.persistence.jpa.JpaMedicationRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MedicationRepositoryAdapter(
    private val jpaMedicationRepository: JpaMedicationRepository
) : MedicationRepository {

    override fun save(medication: Medication): Medication {
        val entity = medication.toEntity()
        val saved = jpaMedicationRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): Medication? {
        return jpaMedicationRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Medication> {
        return jpaMedicationRepository.findAll().map { it.toDomain() }
    }

    override fun findByPetId(petId: UUID): List<Medication> {
        return jpaMedicationRepository.findByPetId(petId).map { it.toDomain() }
    }

    override fun findActiveMedicationsByPetId(petId: UUID): List<Medication> {
        return jpaMedicationRepository.findByPetIdAndActive(petId, true).map { it.toDomain() }
    }

    override fun existsById(id: UUID): Boolean {
        return jpaMedicationRepository.existsById(id)
    }

    override fun update(medication: Medication): Medication {
        val entity = medication.toEntity()
        val saved = jpaMedicationRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: UUID) {
        jpaMedicationRepository.deleteById(id)
    }

    private fun Medication.toEntity() = MedicationEntity(
        id = this.id,
        petId = this.petId,
        name = this.name,
        dosage = this.dosage,
        frequency = this.frequency,
        startDate = this.startDate,
        endDate = this.endDate,
        prescribedBy = this.prescribedBy,
        instructions = this.instructions,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun MedicationEntity.toDomain() = Medication(
        id = this.id,
        petId = this.petId,
        name = this.name,
        dosage = this.dosage,
        frequency = this.frequency,
        startDate = this.startDate,
        endDate = this.endDate,
        prescribedBy = this.prescribedBy,
        instructions = this.instructions,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
