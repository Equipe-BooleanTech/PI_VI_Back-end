package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Pet
import java.util.UUID

interface PetRepository {
    fun save(pet: Pet): Pet
    fun findById(id: UUID): Pet?
    fun findAll(): List<Pet>
    fun findByTutorId(tutorId: UUID): List<Pet>
    fun findActiveByTutorId(tutorId: UUID): List<Pet>
    fun existsById(id: UUID): Boolean
    fun delete(id: UUID)
    fun update(pet: Pet): Pet
}
