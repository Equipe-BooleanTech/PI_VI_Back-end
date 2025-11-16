package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.enums.VaccinationStatus
import edu.fatec.petwise.domain.enums.VaccineType
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

interface VaccineRepository {
    fun findAll(): List<Vaccine>
    fun findById(id: UUID): Optional<Vaccine>
    fun findByPetIdOrderByVaccinationDateDesc(petId: UUID?): List<Vaccine>
    fun findByVeterinarianIdOrderByVaccinationDateDesc(veterinarianId: UUID): List<Vaccine>
    fun findByPetIdAndStatus(petId: UUID, status: VaccinationStatus): List<Vaccine>
    fun findByVaccineType(vaccineType: VaccineType): List<Vaccine>
    fun findByPetIdAndVaccinationDateBetween(petId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<Vaccine>
    fun countByPetIdAndVaccineType(petId: UUID, vaccineType: VaccineType): Long
    fun save(vaccine: Vaccine): Vaccine
    fun deleteById(id: UUID)
}
