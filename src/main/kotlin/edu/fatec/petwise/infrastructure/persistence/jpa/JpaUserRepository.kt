package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.domain.entity.UserType
import edu.fatec.petwise.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaUserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
    fun existsByEmail(email: String): Boolean
    fun existsByCpf(cpf: String): Boolean
    fun existsByCrmv(crmv: String): Boolean
    fun existsByCnpj(cnpj: String): Boolean
    fun findAllByUserType(userType: UserType): List<UserEntity>
}
