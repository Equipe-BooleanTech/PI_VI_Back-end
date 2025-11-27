package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Prescription
import java.util.Optional
import java.util.UUID

interface PrescriptionRepository {
    fun findAll(): List<Prescription>
    fun findByPetId(petId: UUID): List<Prescription>
    fun findByUserId(userId: UUID): List<Prescription>
    fun findByVeterinaryId(veterinaryId: UUID): List<Prescription>
    fun findByVeterinaryIdAndPetId(veterinaryId: UUID, petId: UUID): List<Prescription>
    fun findByIdAndUserId(id: UUID, userId: UUID): Prescription?
    fun findById(id: UUID): Optional<Prescription>
    fun existsByPetIdAndVeterinaryIdNot(petId: UUID, veterinaryId: UUID): Boolean
    fun existsByPetId(petId: UUID): Boolean
    fun save(prescription: Prescription): Prescription
    fun deleteById(id: UUID)
    fun deleteByPetId(petId: UUID)
}
