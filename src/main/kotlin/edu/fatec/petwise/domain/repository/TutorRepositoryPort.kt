package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Tutor
import java.util.UUID

interface TutorRepository {
    fun save(tutor: Tutor): Tutor
    fun findById(id: UUID): Tutor?
    fun findAll(): List<Tutor>
    fun findByCpf(cpf: String): Tutor?
    fun findByEmail(email: String): Tutor?
    fun existsById(id: UUID): Boolean
    fun existsByCpf(cpf: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun delete(id: UUID)
    fun update(tutor: Tutor): Tutor
}
