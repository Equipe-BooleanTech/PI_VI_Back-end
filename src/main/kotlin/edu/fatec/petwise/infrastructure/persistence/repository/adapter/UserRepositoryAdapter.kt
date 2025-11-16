package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.User
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import edu.fatec.petwise.infrastructure.persistence.entity.UserEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaUserRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class UserRepositoryAdapter(
    private val repository: JpaUserRepository
): UserRepository {
    override fun save(user: User): User {
        val entity = UserEntity(
            id = user.id,
            fullName = user.fullName,
            email = user.email.value,
            phone = user.phone.value,
            passwordHash = user.passwordHash,
            userType = user.userType,
            cpf = user.cpf,
            crmv = user.crmv,
            specialization = user.specialization,
            cnpj = user.cnpj,
            companyName = user.companyName,
            active = user.active,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
        val saved = repository.save(entity)
        return saved.toDomain()
    }

    override fun findByEmail(email: String): User? = repository.findByEmail(email)?.toDomain()
    override fun existsByEmail(email: String): Boolean = repository.existsByEmail(email)
    override fun existsByCpf(cpf: String): Boolean = repository.existsByCpf(cpf)
    override fun existsByCrmv(crmv: String): Boolean = repository.existsByCrmv(crmv)
    override fun existsByCnpj(cnpj: String): Boolean = repository.existsByCnpj(cnpj)
    override fun findById(id: UUID): Optional<User> = repository.findById(id).map { it.toDomain() }
    override fun deleteById(id: UUID) = repository.deleteById(id)

    private fun UserEntity.toDomain(): User = User(
        id = this.id,
        fullName = this.fullName,
        email = Email(this.email),
        phone = Telefone(this.phone),
        passwordHash = this.passwordHash,
        userType = this.userType,
        cpf = this.cpf,
        crmv = this.crmv,
        specialization = this.specialization,
        cnpj = this.cnpj,
        companyName = this.companyName,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
