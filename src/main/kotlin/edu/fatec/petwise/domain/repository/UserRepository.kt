package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.User
import java.util.UUID

interface UserRepository {
    fun save(user: User): User
    fun findById(id: UUID): User?
    fun findByEmail(email: String): User?
    fun findAll(): List<User>
    fun existsByEmail(email: String): Boolean
    fun existsByCpf(cpf: String): Boolean
    fun existsByCrmv(crmv: String): Boolean
    fun existsByCnpj(cnpj: String): Boolean
    fun existsById(id: UUID): Boolean
    fun update(user: User): User
    fun delete(id: UUID)
}
