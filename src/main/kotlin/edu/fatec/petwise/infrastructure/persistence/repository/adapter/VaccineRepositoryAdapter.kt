package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaVaccineRepository
import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.enums.VaccinationStatus
import edu.fatec.petwise.domain.enums.VaccineType
import edu.fatec.petwise.domain.repository.VaccineRepository
import edu.fatec.petwise.infrastructure.persistence.entity.VaccineEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class VaccineRepositoryAdapter(
    private val repository: JpaVaccineRepository
) : VaccineRepository {

    override fun findAll(): List<Vaccine> {
        return repository.findAll().map { it.toDomain() }
    }

    override fun findById(id: UUID): Optional<Vaccine> {
        return repository.findById(id).map { it.toDomain() }
    }

    override fun findByPetIdOrderByVaccinationDateDesc(petId: UUID?): List<Vaccine> {
        if (petId == null) return emptyList()
        return repository.findByPetIdOrderByVaccinationDateDesc(petId).map { it.toDomain() }
    }

    override fun findByVeterinarianIdOrderByVaccinationDateDesc(veterinarianId: UUID): List<Vaccine> {
        return repository.findByVeterinarianIdOrderByVaccinationDateDesc(veterinarianId).map { it.toDomain() }
    }

    override fun findByPetIdAndStatus(petId: UUID, status: VaccinationStatus): List<Vaccine> {
        return repository.findByPetIdAndStatus(petId, status).map { it.toDomain() }
    }

    override fun findByVaccineType(vaccineType: VaccineType): List<Vaccine> {
        return repository.findByVaccineType(vaccineType).map { it.toDomain() }
    }

    override fun findByPetIdAndVaccinationDateBetween(petId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<Vaccine> {
        return repository.findByPetIdAndVaccinationDateBetween(petId, startDate, endDate).map { it.toDomain() }
    }

    override fun countByPetIdAndVaccineType(petId: UUID, vaccineType: VaccineType): Long {
        return repository.countByPetIdAndVaccineType(petId, vaccineType)
    }

    override fun save(vaccine: Vaccine): Vaccine {
        val entity = vaccine.toEntity()
        return repository.save(entity).toDomain()
    }

    override fun deleteById(id: UUID) {
        repository.deleteById(id)
    }

    private fun VaccineEntity.toDomain(): Vaccine {
        return Vaccine(
            id = this.id,
            petId = this.petId,
            veterinarianId = this.veterinarianId,
            vaccineType = this.vaccineType,
            vaccinationDate = this.vaccinationDate,
            nextDoseDate = this.nextDoseDate,
            totalDoses = this.totalDoses,
            manufacturer = this.manufacturer,
            observations = this.observations,
            status = this.status,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun Vaccine.toEntity(): VaccineEntity {
        return VaccineEntity(
            id = this.id,
            petId = this.petId,
            veterinarianId = this.veterinarianId,
            vaccineType = this.vaccineType,
            vaccinationDate = this.vaccinationDate,
            nextDoseDate = this.nextDoseDate,
            totalDoses = this.totalDoses,
            manufacturer = this.manufacturer,
            observations = this.observations,
            status = this.status,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
