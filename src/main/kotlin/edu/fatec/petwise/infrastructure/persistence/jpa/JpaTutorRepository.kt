package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.TutorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaTutorRepository : JpaRepository<TutorEntity, UUID> {
    fun findByCpf(cpf: String): TutorEntity?
    fun findByEmail(email: String): TutorEntity?
    fun existsByCpf(cpf: String): Boolean
    fun existsByEmail(email: String): Boolean
}
