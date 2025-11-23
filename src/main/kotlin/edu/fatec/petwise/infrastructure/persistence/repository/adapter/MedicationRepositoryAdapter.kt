package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.Medication
import edu.fatec.petwise.domain.entity.MedicationFilterOptions
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.infrastructure.persistence.entity.MedicationEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaMedicationRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class MedicationRepositoryAdapter(
    private val repository: JpaMedicationRepository
) : MedicationRepository {

    override fun findById(id: UUID): Optional<Medication> {
        return repository.findById(id).map { it.toDomain() }
    }

    override fun existsById(id: UUID): Boolean {
        return repository.existsById(id)
    }

    override fun findByUserId(userId: UUID): List<Medication> {
        return repository.findByUserId(userId).map { it.toDomain() }
    }

    override fun findByPrescriptionId(prescriptionId: UUID): List<Medication> {
        return repository.findByPrescriptionId(prescriptionId).map { it.toDomain() }
    }

    override fun findByPetId(petId: UUID): List<Medication> {
        return repository.findByPetId(petId).map { it.toDomain() }
    }

    override fun save(medication: Medication): Medication {
        val entity = medication.toEntity()
        return repository.save(entity).toDomain()
    }

    override fun filterMedications(options: MedicationFilterOptions): List<Medication> {
        var meds = repository.findAll().map { it.toDomain() }
        options.petId?.let { id -> meds = meds.filter { it.userId == id } }
        options.medicationName?.let { name -> meds = meds.filter { it.medicationName.contains(name, ignoreCase = true) } }
        options.status?.let { status -> meds = meds.filter { it.status == status } }
        options.startDate?.let { start -> meds = meds.filter { it.startDate >= start } }
        options.endDate?.let { end -> meds = meds.filter { it.endDate <= end } }
        options.searchQuery?.let { query -> meds = meds.filter { it.medicationName.contains(query, ignoreCase = true) || it.dosage.contains(query, ignoreCase = true) } }
        return meds
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    override fun deleteByPrescriptionId(prescriptionId: UUID) {
        repository.deleteByPrescriptionId(prescriptionId)
    }

    private fun MedicationEntity.toDomain(): Medication {
        return Medication(
            id = this.id,
            userId = this.userId,
            prescriptionId = this.prescriptionId,
            medicationName = this.medicationName,
            dosage = this.dosage,
            frequency = this.frequency,
            durationDays = this.durationDays,
            startDate = this.startDate,
            endDate = this.endDate,
            sideEffects = this.sideEffects,
            status = this.status,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun Medication.toEntity(): MedicationEntity {
        return MedicationEntity(
            id = this.id,
            userId = this.userId,
            prescriptionId = this.prescriptionId,
            medicationName = this.medicationName,
            dosage = this.dosage,
            frequency = this.frequency,
            durationDays = this.durationDays,
            startDate = this.startDate,
            endDate = this.endDate,
            sideEffects = this.sideEffects,
            status = this.status,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
