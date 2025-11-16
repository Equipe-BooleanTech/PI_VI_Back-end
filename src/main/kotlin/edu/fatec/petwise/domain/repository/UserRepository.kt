package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.User
import java.util.Optional
import java.util.UUID

interface UserRepository {
    fun save(user: User): User
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByCpf(cpf: String): Boolean
    fun existsByCrmv(crmv: String): Boolean
    fun existsByCnpj(cnpj: String): Boolean
    fun findById(id: UUID): Optional<User>
    fun deleteById(id: UUID)

}
