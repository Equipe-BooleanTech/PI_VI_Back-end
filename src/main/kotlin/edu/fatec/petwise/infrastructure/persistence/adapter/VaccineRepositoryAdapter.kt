package edu.fatec.petwise.infrastructure.persistence.adapter

import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.repository.VaccineRepository
import edu.fatec.petwise.infrastructure.persistence.entity.VaccineEntity
import edu.fatec.petwise.infrastructure.persistence.jpa.JpaVaccineRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class VaccineRepositoryAdapter(
    private val jpaVaccineRepository: JpaVaccineRepository
) : VaccineRepository {

    override fun save(vaccine: Vaccine): Vaccine {
        val entity = vaccine.toEntity()
        val saved = jpaVaccineRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): Vaccine? {
        return jpaVaccineRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Vaccine> {
        return jpaVaccineRepository.findAll().map { it.toDomain() }
    }

    override fun findByPetId(petId: UUID): List<Vaccine> {
        return jpaVaccineRepository.findByPetId(petId).map { it.toDomain() }
    }

    override fun findDueVaccinesByPetId(petId: UUID): List<Vaccine> {
        return jpaVaccineRepository.findByPetId(petId).map { it.toDomain() }
            .filter { it.nextDoseDate != null && it.nextDoseDate!!.isNotEmpty() }
    }

    override fun existsById(id: UUID): Boolean {
        return jpaVaccineRepository.existsById(id)
    }

    override fun update(vaccine: Vaccine): Vaccine {
        val entity = vaccine.toEntity()
        val saved = jpaVaccineRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: UUID) {
        jpaVaccineRepository.deleteById(id)
    }

    private fun Vaccine.toEntity() = VaccineEntity(
        id = this.id,
        petId = this.petId,
        vaccineName = this.vaccineName,
        vaccineType = this.vaccineType,
        applicationDate = this.applicationDate,
        nextDoseDate = this.nextDoseDate,
        doseNumber = this.doseNumber,
        totalDoses = this.totalDoses,
        veterinaryId = this.veterinaryId,
        clinicName = this.clinicName,
        batchNumber = this.batchNumber,
        manufacturer = this.manufacturer,
        observations = this.observations,
        sideEffects = this.sideEffects,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun VaccineEntity.toDomain() = Vaccine(
        id = this.id,
        petId = this.petId,
        vaccineName = this.vaccineName,
        vaccineType = this.vaccineType,
        applicationDate = this.applicationDate,
        nextDoseDate = this.nextDoseDate,
        doseNumber = this.doseNumber,
        totalDoses = this.totalDoses,
        veterinaryId = this.veterinaryId,
        clinicName = this.clinicName,
        batchNumber = this.batchNumber,
        manufacturer = this.manufacturer,
        observations = this.observations,
        sideEffects = this.sideEffects,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
