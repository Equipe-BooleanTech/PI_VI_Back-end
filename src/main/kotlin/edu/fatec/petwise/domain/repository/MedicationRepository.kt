package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Medication
import java.util.UUID

interface MedicationRepository {
    fun save(medication: Medication): Medication
    fun findById(id: UUID): Medication?
    fun findAll(): List<Medication>
    fun findByPetId(petId: UUID): List<Medication>
    fun findActiveMedicationsByPetId(petId: UUID): List<Medication>
    fun existsById(id: UUID): Boolean
    fun update(medication: Medication): Medication
    fun delete(id: UUID)
}
