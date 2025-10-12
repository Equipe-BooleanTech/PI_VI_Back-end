package edu.fatec.petwise.infrastructure.persistence.adapter

import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.repository.VaccineRepository
import edu.fatec.petwise.infrastructure.persistence.entity.VaccineEntity
import edu.fatec.petwise.infrastructure.persistence.jpa.JpaVaccineRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
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
        return jpaVaccineRepository.findDueVaccinesByPetIdAndDate(petId, LocalDate.now()).map { it.toDomain() }
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
        name = this.name,
        manufacturer = this.manufacturer,
        batchNumber = this.batchNumber,
        applicationDate = this.applicationDate,
        nextDoseDate = this.nextDoseDate,
        veterinaryId = this.veterinaryId,
        notes = this.notes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun VaccineEntity.toDomain() = Vaccine(
        id = this.id,
        petId = this.petId,
        name = this.name,
        manufacturer = this.manufacturer,
        batchNumber = this.batchNumber,
        applicationDate = this.applicationDate,
        nextDoseDate = this.nextDoseDate,
        veterinaryId = this.veterinaryId,
        notes = this.notes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
