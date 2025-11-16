package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Medication
import edu.fatec.petwise.domain.entity.MedicationFilterOptions
import java.util.Optional
import java.util.UUID

interface MedicationRepository {
    fun existsById(id: UUID): Boolean
    fun findByUserId(userId: UUID): List<Medication>
    fun findByPrescriptionId(prescriptionId: UUID): List<Medication>
    fun findById(id: UUID): Optional<Medication>
    fun save(medication: Medication): Medication
    fun filterMedications(options: MedicationFilterOptions): List<Medication>
    fun deleteById(id: UUID)
}
