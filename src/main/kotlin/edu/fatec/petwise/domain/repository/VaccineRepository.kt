package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Vaccine
import java.time.LocalDate
import java.util.UUID

interface VaccineRepository {
    fun save(vaccine: Vaccine): Vaccine
    fun findById(id: UUID): Vaccine?
    fun findAll(): List<Vaccine>
    fun findByPetId(petId: UUID): List<Vaccine>
    fun findDueVaccinesByPetId(petId: UUID): List<Vaccine>
    fun existsById(id: UUID): Boolean
    fun update(vaccine: Vaccine): Vaccine
    fun delete(id: UUID)
    fun countPendingVaccines(ownerId: UUID): Int
    fun findPendingVaccinesByPet(petId: UUID): List<Vaccine>
    fun findLastDewormingByPet(petId: UUID): LocalDate?
}
