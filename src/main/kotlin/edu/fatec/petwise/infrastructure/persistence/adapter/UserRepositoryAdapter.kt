package edu.fatec.petwise.infrastructure.persistence.adapter

import edu.fatec.petwise.domain.entity.User
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import edu.fatec.petwise.infrastructure.persistence.entity.UserEntity
import edu.fatec.petwise.infrastructure.persistence.jpa.JpaUserRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserRepositoryAdapter(
    private val jpaUserRepository: JpaUserRepository
) : UserRepository {

    override fun save(user: User): User {
        val entity = user.toEntity()
        val saved = jpaUserRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): User? {
        return jpaUserRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByEmail(email: String): User? {
        return jpaUserRepository.findByEmail(email)?.toDomain()
    }

    override fun findAll(): List<User> {
        return jpaUserRepository.findAll().map { it.toDomain() }
    }

    override fun existsByEmail(email: String): Boolean {
        return jpaUserRepository.existsByEmail(email)
    }

    override fun existsByCpf(cpf: String): Boolean {
        return jpaUserRepository.existsByCpf(cpf)
    }

    override fun existsByCrmv(crmv: String): Boolean {
        return jpaUserRepository.existsByCrmv(crmv)
    }

    override fun existsByCnpj(cnpj: String): Boolean {
        return jpaUserRepository.existsByCnpj(cnpj)
    }

    override fun existsById(id: UUID): Boolean {
        return jpaUserRepository.existsById(id)
    }

    override fun update(user: User): User {
        val entity = user.toEntity()
        val saved = jpaUserRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: UUID) {
        jpaUserRepository.deleteById(id)
    }

    private fun User.toEntity() = UserEntity(
        id = this.id,
        fullName = this.fullName,
        email = this.email.value,
        phone = this.phone.value,
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

    private fun UserEntity.toDomain() = User(
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
