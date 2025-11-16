package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.VaccineEntity
import edu.fatec.petwise.domain.enums.VaccinationStatus
import edu.fatec.petwise.domain.enums.VaccineType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaVaccineRepository : JpaRepository<VaccineEntity, UUID> {
    fun findByPetIdOrderByVaccinationDateDesc(petId: UUID): List<VaccineEntity>
    fun findByVeterinarianIdOrderByVaccinationDateDesc(veterinarianId: UUID): List<VaccineEntity>
    fun findByPetIdAndStatus(petId: UUID, status: VaccinationStatus): List<VaccineEntity>
    fun findByVaccineType(vaccineType: VaccineType): List<VaccineEntity>
    fun findByPetIdAndVaccinationDateBetween(petId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<VaccineEntity>
    fun countByPetIdAndVaccineType(petId: UUID, vaccineType: VaccineType): Long
}
