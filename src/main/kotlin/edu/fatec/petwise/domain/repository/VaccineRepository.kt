package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Vaccine
import java.util.UUID

interface VaccineRepository {
    fun save(vaccine: Vaccine): Vaccine
    fun findById(id: UUID): Vaccine?
    fun findAll(): List<Vaccine>
    fun findByPetId(petId: UUID): List<Vaccine>
    fun findByOwnerId(ownerId: UUID): List<Vaccine>
    fun countByOwnerId(ownerId: UUID): Int
    fun findDueVaccinesByPetId(petId: UUID): List<Vaccine>
    fun existsById(id: UUID): Boolean
    fun update(vaccine: Vaccine): Vaccine
    fun delete(id: UUID)
}
